package com.synda.traceid;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * 服务提供方的 Dubbo Filter
 * 从 RPC Context 中取出 traceId 添加到 MDC 中，实现业务逻辑处理的日志中携带traceId
 *
 * Created by lsd
 * 2021-09-28 00:32
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -1)
public class LogTraceProviderDubboFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogTraceProviderDubboFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String traceId = RpcContext.getContext().getAttachment(LogTraceUtils.LOGTRACEID);
        if (!StringUtils.isBlank(traceId)) {
            MDC.put(LogTraceUtils.LOGTRACEID, traceId);
        }
        // 调用Service实现，执行业务逻辑
        Result result = invoker.invoke(invocation);
        if (result.hasException()) {
            log.error("com.synda.traceid.LogTraceProviderDubboFilter.invoke() error!", result.getException());
        }
        // 因为线程复用，需要清除线程上下文的traceId
        LogTraceUtils.removeTraceId();
        return result;
    }
}
