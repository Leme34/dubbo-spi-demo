package com.synda.dubbo.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.metrics.*;
import com.alibaba.metrics.common.CollectLevel;
import com.alibaba.metrics.common.MetricObject;
import com.alibaba.metrics.common.MetricsCollector;
import com.alibaba.metrics.common.MetricsCollectorFactory;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.threadpool.manager.ExecutorRepository;
import org.apache.dubbo.common.utils.NetUtils;
import org.apache.dubbo.monitor.MetricsService;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.support.RpcUtils;

import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.apache.dubbo.common.constants.CommonConstants.*;
import static org.apache.dubbo.monitor.Constants.*;

@Activate(group = {CommonConstants.PROVIDER}, order = -1)  //Activate机制 能根据 group 激活一批具体 SPI 扩展实现
public class MyMetricsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(MyMetricsFilter.class);
    private static final AtomicBoolean exported = new AtomicBoolean(false);
    private Integer port;
    private String protocolName;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (exported.compareAndSet(false, true)) {
            this.protocolName = invoker.getUrl().getParameter(METRICS_PROTOCOL) == null ?
                    DEFAULT_PROTOCOL : invoker.getUrl().getParameter(METRICS_PROTOCOL);

            Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(protocolName);

            this.port = invoker.getUrl().getParameter(METRICS_PORT) == null ?
                    protocol.getDefaultPort() : Integer.parseInt(invoker.getUrl().getParameter(METRICS_PORT));

            Invoker<MetricsService> metricsInvoker = initMetricsInvoker();

            try {
                protocol.export(metricsInvoker);
            } catch (RuntimeException e) {
                logger.error("Metrics Service need to be configured" +
                        " when multiple processes are running on a host" + e.getMessage());
            }
        }

        RpcContext context = RpcContext.getContext();
        boolean isProvider = context.isProviderSide();
        long start = System.currentTimeMillis();
        try {
            Result result = invoker.invoke(invocation); // proceed invocation chain
            long duration = System.currentTimeMillis() - start;
            reportMetrics(invoker, invocation, duration, "success", isProvider);
            return result;
        } catch (RpcException e) {
            long duration = System.currentTimeMillis() - start;
            String result = "error";
            if (e.isTimeout()) {
                result = "timeoutError";
            }
            if (e.isBiz()) {
                result = "bisError";
            }
            if (e.isNetwork()) {
                result = "networkError";
            }
            if (e.isSerialization()) {
                result = "serializationError";
            }
            reportMetrics(invoker, invocation, duration, result, isProvider);
            throw e;
        }
    }

    private String buildMethodName(Invocation invocation) {
        String methodName = RpcUtils.getMethodName(invocation);
        StringBuilder method = new StringBuilder(methodName);
        Class<?>[] argTypes = RpcUtils.getParameterTypes(invocation);

        method.append("(");

        for (int i = 0; i < argTypes.length; i++) {
            method.append(i == 0 ? "" : ", ").append(argTypes[i].getSimpleName());
        }
        method.append(")");
        Class<?> returnType = RpcUtils.getReturnType(invocation);
        String typeName = null;
        if(returnType != null) {
            typeName = returnType.getTypeName();
            typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
        }

        return (typeName == null ? "void" : typeName) + " " + method;
    }

    private void reportMetrics(Invoker<?> invoker, Invocation invocation, long duration, String result, boolean isProvider) {
        String serviceName = invoker.getInterface().getName();
        String methodName = buildMethodName(invocation);
        MetricName global;
        MetricName method;
        if (isProvider) {
            global = new MetricName(DUBBO_PROVIDER, MetricLevel.MAJOR);
            method = new MetricName(DUBBO_PROVIDER_METHOD, new HashMap<String, String>(4) {
                {
                    put(SERVICE, serviceName);
                    put(METHOD, methodName);
                }
            }, MetricLevel.NORMAL);
        } else {
            global = new MetricName(DUBBO_CONSUMER, MetricLevel.MAJOR);
            method = new MetricName(DUBBO_CONSUMER_METHOD, new HashMap<String, String>(4) {
                {
                    put(SERVICE, serviceName);
                    put(METHOD, methodName);
                }
            }, MetricLevel.NORMAL);
        }
        setCompassQuantity(DUBBO_GROUP, result, duration, global, method);
    }

    private void setCompassQuantity(String groupName, String result, long duration, MetricName... metricNames) {
        for (MetricName metricName : metricNames) {
            FastCompass compass = MetricManager.getFastCompass(groupName, metricName);
            compass.record(duration, result);
        }
    }

    private List<MetricObject> getThreadPoolMessage() {
//        DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
//        Map<String, Object> executors = dataStore.get(EXECUTOR_SERVICE_COMPONENT_KEY);
//
//        List<MetricObject> threadPoolMetricList = new ArrayList<>();
//        for (Map.Entry<String, Object> entry : executors.entrySet()) {
//            ExecutorService executor = (ExecutorService) entry.getValue();
//            if (executor instanceof ThreadPoolExecutor) {
//                ThreadPoolExecutor tp = (ThreadPoolExecutor) executor;
//
//                threadPoolMetricList.add(value2MetricObject("threadPool.active", tp.getActiveCount(), MetricLevel.MAJOR));
//                threadPoolMetricList.add(value2MetricObject("threadPool.core", tp.getCorePoolSize(), MetricLevel.MAJOR));
//                threadPoolMetricList.add(value2MetricObject("threadPool.max", tp.getMaximumPoolSize(), MetricLevel.MAJOR));
//                threadPoolMetricList.add(value2MetricObject("threadPool.current", tp.getPoolSize(), MetricLevel.MAJOR));
//            }
//        }

        // 改用Dubbo2.7.3+版本获取线程池对象的方式，以上获取方式只适用于低版本
        List<MetricObject> threadPoolMetricList = new ArrayList<>();
        ExecutorRepository executorRepo = ExtensionLoader.getExtensionLoader(ExecutorRepository.class).getDefaultExtension();
        ExecutorService executorService;
        try {
            executorService = executorRepo.getExecutor(new URL("dubbo", InetAddress.getLocalHost().getHostAddress(), this.port));
        } catch (Exception e) {
            logger.error("get threadPool metrics failed!",e);
            return threadPoolMetricList;
        }
        if (executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor executor = (ThreadPoolExecutor) executorService;
            int poolSize = executor.getPoolSize();
            int activeCount = executor.getActiveCount();
            int maxPoolSize = executor.getMaximumPoolSize();
            threadPoolMetricList.add(value2MetricObject("threadPool.active", activeCount, MetricLevel.MAJOR));
            threadPoolMetricList.add(value2MetricObject("threadPool.core", executor.getCorePoolSize(), MetricLevel.MAJOR));
            threadPoolMetricList.add(value2MetricObject("threadPool.max", maxPoolSize, MetricLevel.MAJOR));
            threadPoolMetricList.add(value2MetricObject("threadPool.current", poolSize, MetricLevel.MAJOR));
        }

        return threadPoolMetricList;
    }

    private MetricObject value2MetricObject(String metric, Integer value, MetricLevel level) {
        if (metric == null || value == null || level == null) {
            return null;
        }

        return new MetricObject
                .Builder(metric)
                .withValue(value)
                .withLevel(level)
                .build();
    }

    private Invoker<MetricsService> initMetricsInvoker() {
        return new Invoker<MetricsService>() {
            @Override
            public Class<MetricsService> getInterface() {
                return MetricsService.class;
            }

            @Override
            public Result invoke(Invocation invocation) throws RpcException {
                String group = invocation.getArguments()[0].toString();
                MetricRegistry registry = MetricManager.getIMetricManager().getMetricRegistryByGroup(group);

                SortedMap<MetricName, FastCompass> fastCompasses = registry.getFastCompasses();

                long timestamp = System.currentTimeMillis();
                double rateFactor = TimeUnit.SECONDS.toSeconds(1);
                double durationFactor = 1.0 / TimeUnit.MILLISECONDS.toNanos(1);


                MetricsCollector collector = MetricsCollectorFactory.createNew(
                        CollectLevel.NORMAL, Collections.EMPTY_MAP, rateFactor, durationFactor, null);

                for (Map.Entry<MetricName, FastCompass> entry : fastCompasses.entrySet()) {
                    collector.collect(entry.getKey(), entry.getValue(), timestamp);
                }

                List<MetricObject> res = collector.build();
                res.addAll(getThreadPoolMessage());
                return AsyncRpcResult.newDefaultAsyncResult(JSON.toJSONString(res), invocation);
            }

            @Override
            public URL getUrl() {
                return URL.valueOf(protocolName + "://" + NetUtils.getIpByConfig() + ":" + port + "/" + MetricsService.class.getName());
            }

            @Override
            public boolean isAvailable() {
                return false;
            }

            @Override
            public void destroy() {

            }
        };
    }
}