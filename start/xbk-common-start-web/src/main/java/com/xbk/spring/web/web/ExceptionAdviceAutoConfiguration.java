package com.xbk.spring.web.web;

import com.xbk.core.global.exception.BizException;
import com.xbk.core.global.result.CommonResponse;
import com.xbk.core.global.result.FailResponse;
import com.xbk.core.global.result.ResponseCommon;
import com.xbk.spring.web.exception.ExceptionAutoConfiguration;
import com.xbk.spring.web.exception.handler.ExceptionResultHandler;
import com.xbk.spring.web.exception.resolver.ExceptionResolver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Set;

/**
 * 统一异常处理
 *
 * @author junli
 * @version 1.0
 * @date Created in 2019年12月18日 20:24
 * @since 1.0
 */
@Configuration
@Import(ExceptionAutoConfiguration.class)
@Slf4j
@RestControllerAdvice
public class ExceptionAdviceAutoConfiguration implements InitializingBean, Ordered {

    @Autowired(required = false)
    public List<ExceptionResolver> exceptionResolverList;

    @Autowired
    public ExceptionResultHandler exceptionResultHandler;

    /**
     * 业务异常信息
     */
    @ExceptionHandler(BizException.class)
    public CommonResponse bizExceptionHandler(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception) {
        FailResponse failResponse = FailResponse.of((BizException) exception);
        return resolverAndHandle(request, handlerMethod, exception, HttpStatus.OK, failResponse);
    }

    /**
     * validator 异常处理
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResponse argumentNotValidException(HttpServletRequest request, HandlerMethod handlerMethod, MethodArgumentNotValidException validException) {
        List<ObjectError> list = validException.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(list) && ObjectUtils.allNotNull(list.get(0))) {
            ObjectError objectError = list.get(0);
            return resolverAndHandle(request, handlerMethod,
                    BizException.builder().message(objectError.getDefaultMessage()).build(),
                    HttpStatus.BAD_REQUEST,
                    FailResponse.of(ResponseCommon.INVALID_ARGUMENTS.getCode(), objectError.getDefaultMessage()));
        }
        return resolverAndHandle(request, handlerMethod, validException, HttpStatus.BAD_REQUEST, ResponseCommon.INVALID_ARGUMENTS);
    }

    /**
     * 参数绑定验证
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public CommonResponse constraintViolationException(HttpServletRequest request, HandlerMethod handlerMethod, ConstraintViolationException validException) {
        Set<ConstraintViolation<?>> set = validException.getConstraintViolations();
        if (!CollectionUtils.isEmpty(set)) {
            ConstraintViolation constraintViolation = set.stream().findFirst().get();
            return resolverAndHandle(request, handlerMethod,
                    BizException.builder().message(constraintViolation.getMessage()).build(),
                    HttpStatus.BAD_REQUEST,
                    FailResponse.of(ResponseCommon.INVALID_ARGUMENTS.getCode(), constraintViolation.getMessage()));
        }
        return resolverAndHandle(request, handlerMethod, validException, HttpStatus.BAD_REQUEST, ResponseCommon.INVALID_ARGUMENTS);
    }


    /**
     * 请求参数异常
     */
    @ExceptionHandler(value = {ServletException.class, BindException.class,
            ValidationException.class, javax.validation.ValidationException.class,
            HttpMessageConversionException.class, MethodArgumentTypeMismatchException.class})
    public CommonResponse invalidArgumentsHandler(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception) {
        return resolverAndHandle(request, handlerMethod, exception, HttpStatus.BAD_REQUEST, ResponseCommon.INVALID_ARGUMENTS);
    }

    /**
     * 其他异常
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, NoHandlerFoundException.class})
    public CommonResponse notFoundHandler(HttpServletRequest request, Exception exception) {
        return resolverAndHandle(request, null, exception, HttpStatus.NOT_FOUND, ResponseCommon.RESOURCE_NOT_FOUND);
    }

    /**
     * 系统异常信息
     */
    @ExceptionHandler(Throwable.class)
    public CommonResponse exceptionHandler(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception) {
        return resolverAndHandle(request, handlerMethod, exception, HttpStatus.INTERNAL_SERVER_ERROR, ResponseCommon.INTERNAL_SERVER_ERROR);
    }

    @Override
    public void afterPropertiesSet() {
        AnnotationAwareOrderComparator.sort(exceptionResolverList);
    }

    /**
     * 异常信息处理及转换
     */
    private CommonResponse resolverAndHandle(HttpServletRequest request, HandlerMethod handlerMethod, Exception exception, HttpStatus httpStatus, CommonResponse result) {
        if (!CollectionUtils.isEmpty(exceptionResolverList)) {
            for (ExceptionResolver resolver : exceptionResolverList) {
                try {
                    if (resolver.canResolve(request, exception, httpStatus)) {
                        resolver.resolve(request, exception);
                    }
                } catch (Exception e) {
                    log.info("异常处理失败: ExceptionResolver error e = {}", e);
                }
            }
        }
        return exceptionResultHandler.handle(request, handlerMethod, exception, httpStatus, result);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 100;
    }

}
