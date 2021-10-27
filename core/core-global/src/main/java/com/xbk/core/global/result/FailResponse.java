package com.xbk.core.global.result;

import com.xbk.core.global.exception.BizException;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class FailResponse extends CommonResponse {

    /**
     * 失败状态码
     */
    private static final Integer FAIL_CODE = 500;

    /**
     * 失败描述信息
     */
    private static final String FAIL_MESSAGE = "失败";

    /**
     * 响应失败 状态值
     */
    private static final String FAIL_STATUS = "fail";

    /**
     * 出现RuntimeException 时 异常信息
     */
    private static final String RUNTIME_EXCEPTION_MESSAGE = "未知错误";


    /**
     * 兼容原始错误 code
     */
    private Integer error_code;

    /**
     * 兼容原始错误 message
     */
    private String error_message;

    @Builder
    public FailResponse(Integer code, String status, String message, Object data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.error_code = code;
        this.error_message = message;
        this.data = data;
    }

    /**
     * 默认的错误返回结果
     *
     * @return 错误返回
     */
    public static FailResponse defaultFailResponse() {
        return new FailResponse(FAIL_CODE, FAIL_STATUS, FAIL_MESSAGE, null);
    }

    /**
     * 返回错误结果集
     *
     * @param code    状态码
     * @param status  状态值
     * @param message 返回信息
     * @return 错误返回
     */
    public static FailResponse of(Integer code, String status, String message) {
        return new FailResponse(code, status, message, null);
    }

    /**
     * 返回错误结果集
     *
     * @param code    状态码
     * @param message 状态值
     * @return 错误返回
     */
    public static FailResponse of(Integer code, String message) {
        return new FailResponse(code, FAIL_STATUS, message, null);
    }

    /**
     * 错误返回结果集
     *
     * @param code 状态码
     * @return 错误返回
     */
    public static FailResponse of(Integer code) {
        return new FailResponse(code, FAIL_STATUS, FAIL_MESSAGE, null);
    }

    /**
     * 错误返回结果集
     *
     * @param message 错误信息
     * @return 错误返回
     */
    public static FailResponse of(String message) {
        return new FailResponse(FAIL_CODE, FAIL_STATUS, message, null);
    }

    /**
     * 运行时异常
     *
     * @param code             状态码
     * @return FailResponse
     */
    public static FailResponse buildRuntimeResponse(Integer code, String message) {
        FailResponse failResponse = new FailResponse(code, FAIL_STATUS, message, null);
        return failResponse;
    }

    /**
     * 错误返回结果集
     *
     * @param bizException 业务异常
     * @return 错误返回
     */
    public static FailResponse of(BizException bizException) {
        return new FailResponse(bizException.getCode(), FAIL_STATUS, bizException.getMessage(), null);
    }

    public static Integer getFailCode() {
        return FAIL_CODE;
    }

    public static String getFailMessage() {
        return FAIL_MESSAGE;
    }

    public static String getFailStatus() {
        return FAIL_STATUS;
    }

    public Integer getError_code() {
        return error_code;
    }

    public String getError_message() {
        return error_message;
    }

    @Override
    public String toString() {
        return String.format("{ \"code\": %s ,\"status\": \"%s\",\"message\": \"%s\", \"data\": \"%s\",\"error_code\": \"%s\",\"error_message\": \"%s\"}",
                code, status, message, Objects.isNull(data) ? "null" : data.toString(), error_code, error_message);
    }

}
