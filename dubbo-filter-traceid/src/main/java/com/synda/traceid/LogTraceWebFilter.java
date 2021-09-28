package com.synda.traceid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;

/**
 * 为本次web请求生成traceId，并放入MDC
 *
 * Created by lsd
 * 2021-09-28 00:22
 */
public class LogTraceWebFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(LogTraceWebFilter.class);

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            LogTraceUtils.addTraceId2MDC();
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.error("com.synda.traceid.LogTraceWebFilter.doFilter() error!", e);
        } finally {
            LogTraceUtils.removeTraceId();
        }
    }
}
