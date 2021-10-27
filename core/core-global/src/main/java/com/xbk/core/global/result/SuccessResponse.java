package com.xbk.core.global.result;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

@Data
@EqualsAndHashCode(callSuper = true)
public class SuccessResponse extends CommonResponse {

    /**
     * 成功状态码
     */
    private static final Integer SUCCESS_CODE = 200;

    /**
     * 兼容原始接口
     */
    private static final Integer SUCCESS_ERROR_CODE = 0;

    /**
     * 成功描述信息
     */
    private static final String SUCCESS_MESSAGE = "成功";

    /**
     * 成功状态值
     */
    private static final String SUCCESS_STATUS = "success";


    /**
     * 兼容原始错误 code
     */
    private Integer error_code;

    /**
     * 兼容原始错误 message
     */
    private String error_message;

    @Builder
    public SuccessResponse(Integer code, String status, String message, Object data, Integer error_code, String error_message) {
        super(code, status, message, data);
        this.error_code = error_code;
        this.error_message = error_message;
    }

    /**
     * 默认返回成功信息
     */
    public static SuccessResponse defaultSuccessResponse() {
        return SuccessResponse.builder()
                .code(SUCCESS_CODE)
                .error_code(SUCCESS_ERROR_CODE)
                .status(SUCCESS_STATUS)
                .message(SUCCESS_MESSAGE)
                .error_message(SUCCESS_MESSAGE)
                .build();
    }

    /**
     * 通过 返回数据构建成返回对象
     *
     * @param data 返回数据
     * @return 返回结果
     */
    public static SuccessResponse of(Object data) {
        return SuccessResponse.builder()
                .code(SUCCESS_CODE)
                .error_code(SUCCESS_ERROR_CODE)
                .status(SUCCESS_STATUS)
                .message(SUCCESS_MESSAGE)
                .error_message(SUCCESS_MESSAGE)
                .data(data)
                .build();
    }


    public static Integer getSuccessCode() {
        return SUCCESS_CODE;
    }

    public static String getSuccessMessage() {
        return SUCCESS_MESSAGE;
    }

    public static String getSuccessStatus() {
        return SUCCESS_STATUS;
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