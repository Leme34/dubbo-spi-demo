package com.synda.traceid;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 服务消费方的 Dubbo Filter
 * 从 MDC 取出 traceId 保存到 RPC Context 中，使得服务提供方能够获取到 traceId
 *
 * Created by lsd
 * 2021-09-28 00:32
 */
@Activate(group = {CommonConstants.CONSUMER}, order = -1)
public class LogTraceConsumerDubboFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(LogTraceConsumerDubboFilter.class);

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            String traceId = LogTraceUtils.getTraceIdFromMDC();
            if (StringUtils.isBlank(traceId)) {
                LogTraceUtils.addTraceId2MDC();
            }
            RpcContext.getContext().setAttachment(LogTraceUtils.LOGTRACEID,LogTraceUtils.getTraceIdFromMDC());
        } catch (Exception e) {
            log.error("com.synda.traceid.LogTraceConsumerDubboFilter.invoke() error!", e);
        }
        // 发起dubbo调用
        return invoker.invoke(invocation);
    }
}
