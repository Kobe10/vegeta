package com.xbk.spring.web.exception.resolver;

import com.xbk.spring.web.util.HttpStatusUtil;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常收集-分析器 该实现不会对最终的异常处理有影响
 */
public interface ExceptionResolver {

    /**
     * 自定义异常处理
     */
    void resolve(HttpServletRequest request, Exception exception);

    /**
     * 是否可以被处理
     */
    boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus);

    /**
     * 获取请求状态码
     */
    default HttpStatus status(HttpServletRequest request) {
        return HttpStatusUtil.getStatus(request);
    }

}
