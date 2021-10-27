package com.xbk.spring.web.exception;

import com.xbk.spring.web.exception.handler.DefaultExceptionResultHandler;
import com.xbk.spring.web.exception.handler.ExceptionResultHandler;
import com.xbk.spring.web.exception.resolver.DefaultBizExceptionResolver;
import com.xbk.spring.web.exception.resolver.DefaultErrorCodeExceptionResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 异常处理自动注入
 */
@Configuration
public class ExceptionAutoConfiguration {

    /**
     * 默认注入的 统一异常结果集处理 bean Name;
     * 方便项目扩展替换
     */
    public static final String DEFAULT_EXCEPTION_RESULT_HANDLER = "defaultExceptionResultHandler";

    /**
     * 默认注入 统一 RuntimeException 分析器
     */
    public static final String DEFAULT_RUNTIME_EXCEPTION_RESOLVER = "defaultRuntimeExceptionResolver";

    /**
     * 默认注入 统一 BizException 分析器
     */
    public static final String DEFAULT_BIZ_EXCEPTION_RESOLVER = "defaultBizExceptionResolver";

    /**
     * 默认注入 统一 DingDingException 分析器
     */
    public static final String DEFAULT_DING_DING_EXCEPTION_RESOLVER = "defaultDingDingExceptionResolver";

    /**
     * 默认注入 统一 1xx 3xx 4xx 分析器
     */
    public static final String DEFAULT_ERROR_CODE_EXCEPTION_RESOLVER = "defaultErrorCodeExceptionResolver";

    /**
     * 统一异常结果处理
     */
    @Configuration
    static class ExceptionResultHandlerConfiguration {

        /**
         * 默认异常结果处理
         */
        @Bean
        @ConditionalOnMissingBean(name = DEFAULT_EXCEPTION_RESULT_HANDLER)
        public ExceptionResultHandler defaultExceptionResultHandler() {
            return new DefaultExceptionResultHandler();
        }

    }

    /**
     * 统一异常过程处理
     */
    @Configuration
    static class ExceptionResolverConfiguration {
        @Bean
        @ConditionalOnMissingBean(name = DEFAULT_BIZ_EXCEPTION_RESOLVER)
        public DefaultBizExceptionResolver defaultBizExceptionResolver() {
            return new DefaultBizExceptionResolver();
        }

        @Bean
        @ConditionalOnMissingBean(name = DEFAULT_ERROR_CODE_EXCEPTION_RESOLVER)
        public DefaultErrorCodeExceptionResolver defaultErrorCodeExceptionResolver() {
            return new DefaultErrorCodeExceptionResolver();
        }
    }
}
