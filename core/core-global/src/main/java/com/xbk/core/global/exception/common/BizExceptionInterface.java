package com.xbk.core.global.exception.common;

import com.xbk.core.global.exception.BizException;

import java.util.Objects;

public interface BizExceptionInterface {

    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取描述信息
     */
    String getMessage();

    /**
     * 获取当前状态码异常
     */
    default BizException build() {
        return BizException.builder()
                .code(getCode())
                .message(getMessage())
                .build();
    }

    /**
     * 获取当前状态异常
     *
     * @param message 补充信息
     * @return 状态那异常
     */
    default BizException build(String... message) {
        String messageInfo = getMessage();
        if (Objects.nonNull(message) && message.length > 0) {
            messageInfo = String.format(messageInfo, (Object[]) message);
        }
        return BizException.builder()
                .code(getCode())
                .message(messageInfo)
                .build();
    }
}
