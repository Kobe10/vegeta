package com.xbk.spring.mybatis.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class HelperException extends RuntimeException {

    /**
     * 编码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

}
