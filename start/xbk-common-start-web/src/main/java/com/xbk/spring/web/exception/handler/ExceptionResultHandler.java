package com.xbk.spring.web.exception.handler;

import com.xbk.core.global.result.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;

public interface ExceptionResultHandler {

    /**
     * exception 后置处理器
     */
    CommonResponse handle(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception, HttpStatus httpStatus, CommonResponse commonResponse);

}
