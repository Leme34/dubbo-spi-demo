package com.synda.traceid;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * Created by lsd
 * 2021-09-28 00:27
 */
public class LogTraceUtils {

    public static final String LOGTRACEID = "LogSessionId";

    public static void addTraceId2MDC(){
        String traceId = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        MDC.put(LOGTRACEID,traceId);
    }

    public static String getTraceIdFromMDC(){
        return MDC.get(LOGTRACEID);
    }

    public static void removeTraceId(){
        MDC.remove(LOGTRACEID);
    }

}
