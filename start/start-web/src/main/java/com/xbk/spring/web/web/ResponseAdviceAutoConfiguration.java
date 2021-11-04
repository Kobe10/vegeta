package com.xbk.spring.web.web;

import com.google.common.collect.Sets;
import com.xbk.spring.web.config.base.SupportPathConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 统一返回结果封装  默认注入
 */
@Configuration
@EnableConfigurationProperties(SupportPathConfig.class)
@ConditionalOnMissingBean(ResponseBodyAdvice.class)
@RestControllerAdvice
@RequiredArgsConstructor
public class ResponseAdviceAutoConfiguration extends AbstractResponseAdviceTemplate implements InitializingBean {

    private final SupportPathConfig supportPathConfig;

    /**
     * 支持包装路径
     */
    static Set SUPPORT_PATH = Sets.newHashSet();

    @Override
    public Set<String> supportPath() {
        return SUPPORT_PATH;
    }

    @Override
    public void afterPropertiesSet() {
        SUPPORT_PATH = Arrays.stream(supportPathConfig.getResponse().split(",")).collect(Collectors.toSet());

    }
}
