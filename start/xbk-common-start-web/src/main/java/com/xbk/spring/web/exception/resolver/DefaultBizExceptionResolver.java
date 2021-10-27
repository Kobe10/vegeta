package com.xbk.spring.web.exception.resolver;

import com.xbk.core.global.exception.BizException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 业务异常处理 分析器
 */
@Slf4j
public class DefaultBizExceptionResolver implements ExceptionResolver, Ordered {

    @Override
    public void resolve(HttpServletRequest request, Exception exception) {
        BizException bizException = (BizException) exception;
        log.warn("DefaultBizExceptionResolver : code = {} , message = {}, exceptionInfo = {}",
                bizException.getCode(), bizException.getMessage(),
                ExceptionUtils.getStackTrace(exception));
    }

    @Override
    public boolean canResolve(HttpServletRequest request, Exception exception, HttpStatus httpStatus) {
        return Objects.nonNull(exception) && exception instanceof BizException;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
