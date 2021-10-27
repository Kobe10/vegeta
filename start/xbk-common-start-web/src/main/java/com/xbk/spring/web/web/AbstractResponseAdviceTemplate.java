package com.xbk.spring.web.web;

import com.xbk.core.global.result.CommonResponse;
import com.xbk.core.global.result.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;
import java.util.Set;

/**
 * 全局异常返回结果集封装
 */
@Slf4j
public abstract class AbstractResponseAdviceTemplate implements ResponseBodyAdvice, Ordered {

    /**
     * 增强器前置条件
     * 此处可以使用注解过滤
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        String name = methodParameter.getMethod().getDeclaringClass().getName();
        Set<String> supportPathList = supportPath();
        return !CollectionUtils.isEmpty(supportPathList) &&
                supportPathList.stream().anyMatch((path) -> name.startsWith(path));
    }

    @Override
    public Object beforeBodyWrite(Object response, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (Objects.isNull(response)) {
            return SuccessResponse.of(response);
        }
        if (response instanceof CommonResponse || response instanceof Throwable) {
            return response;
        }
        SuccessResponse successResponse = SuccessResponse.of(response);
        if (response instanceof CharSequence) {
            return successResponse.toString();
        }
        return successResponse;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    /**
     * 需要格式化的结果集
     *
     * @return 请求包路径
     */
    public abstract Set<String> supportPath();
}
