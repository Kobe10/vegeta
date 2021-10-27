package com.xbk.core.global.log;


import java.util.UUID;

/**
 * 用以存储一个 traceId
 *
 * @Author: lijun
 * @Date: 2020/2/19 17:55
 */
public class LogTraceIdLocal {

    private static ThreadLocal<String> traceId = ThreadLocal.withInitial(() -> "");

    /**
     * 初始化
     */
    public static void init() {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        traceId.set(uuid);
    }

    /**
     * 设置 traceId
     *
     * @param traceIdInfo 传入TraceId
     */
    public static void set(String traceIdInfo) {
        traceId.set(traceIdInfo);
    }

    /**
     * 获取 traceId
     *
     * @return traceId
     */
    public static String get() {
        return traceId.get();
    }

    /**
     * 清除 traceId
     */
    public static void clean() {
        traceId.remove();
    }
}
