package com.xbk.spring.web.exception.resolver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Data
@AllArgsConstructor
public class DefaultErrorCodeExceptionResolver implements ExceptionResolver, Ordered {

    @Override
    public void resolve(HttpServletRequest request, Exception exception) {
        log.warn("DefaultErrorCodeExceptionResolver = {}", exception);
    }

    @Override
    public boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
        return httpStatus.is1xxInformational() || httpStatus.is3xxRedirection() || httpStatus.is4xxClientError();
    }

    /**
     * 排序
     */
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}
