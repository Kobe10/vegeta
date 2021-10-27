package com.xbk.core.global.result;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseCommon {

    /**
     * 默认的成功请求
     */
    public static final CommonResponse SUCCESS = SuccessResponse.defaultSuccessResponse();

    /**
     * 错误请求 - 服务内部错误
     */
    public static final CommonResponse INTERNAL_SERVER_ERROR = FailResponse.of(500, "未知错误");

    /**
     * 错误请求 - 非法的参数
     */
    public static final CommonResponse INVALID_ARGUMENTS = FailResponse.of(511, "非法的参数");

    /**
     * 错误请求 - 请求非法
     */
    public static final CommonResponse FORBIDDEN = FailResponse.of(513, "请求非法");

    /**
     * 错误请求 - 资源未发现
     */
    public static final CommonResponse RESOURCE_NOT_FOUND = FailResponse.of(504, "资源未发现");
}
