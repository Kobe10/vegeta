package com.xbk.spring.web.exception.resolver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * 默认的全局 运行时异常分析
 */
@Slf4j
@Data
@AllArgsConstructor
//@RequiredArgsConstructor
public class DefaultRuntimeExceptionResolver implements ExceptionResolver, Ordered {

    /**
     * broken pipe 信息阻断
     */
    private final String BROKEN_PIPE = "Broken pipe";

    @Override
    public void resolve(HttpServletRequest request, Exception exception) {
        log.error("RunTimeException = {}", ExceptionUtils.getStackTrace(exception));
        //异常报警

    }

    @Override
    public boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
        String message = exception.getMessage();
        boolean isBrokenPipe = BROKEN_PIPE.equals(message);
        return httpStatus.is5xxServerError() && !isBrokenPipe;
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
