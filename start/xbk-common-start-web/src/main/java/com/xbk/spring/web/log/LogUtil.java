package com.xbk.spring.web.log;

import com.xbk.core.global.log.LogTraceIdLocal;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import static com.xbk.core.global.log.LogCommon.TRACE_ID;

/**
 * @Author: lijun
 * @Date: 2020/2/19 21:39
 */
@UtilityClass
public class LogUtil {

    /**
     * 初始化 TraceId
     */
    public static void initTraceId() {
        String traceId = LogTraceIdLocal.get();
        if (StringUtils.isBlank(traceId)) {
            LogTraceIdLocal.init();
            traceId = LogTraceIdLocal.get();
        }
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 初始化 TraceId
     */
    public static void initTraceId(String traceId) {
        MDC.put(TRACE_ID, traceId);
    }

    /**
     * 清理
     */
    public static void clean() {
        MDC.clear();
    }
}
