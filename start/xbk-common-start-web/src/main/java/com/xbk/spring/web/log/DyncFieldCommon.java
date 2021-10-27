package com.xbk.spring.web.log;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DyncFieldCommon {

    /**
     * 类型字段
     */
    public static final String FIELD_TYPE = "type";

    /**
     * 机器地址
     */
    public static final String FIELD_HOST = "host";

    /**
     * 时间消耗
     */
    public static final String FIELD_TIME_COST = "time_cost";

    /**
     * 日志类型 - 正常请求
     */
    public static final String FIELD_TYPE_REQUEST = "request";

    /**
     * 日志类型 - 请求时长
     */
    public static final String FIELD_TYPE_REQUEST_TIME = "requestTime";

}