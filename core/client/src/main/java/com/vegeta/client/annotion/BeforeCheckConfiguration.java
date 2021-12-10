package com.vegeta.client.annotion;

import cn.hutool.core.util.StrUtil;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.exception.ConfigEmptyException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 校验关键配置信息
 *
 * @author fuzhiqiang
 * @date 2021/11/28 22:44
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class BeforeCheckConfiguration {

    @Bean
    public BeforeCheck vegetaBeforeCheckBean(BootstrapProperties properties, ConfigurableEnvironment environment) {
        String namespace = properties.getNamespace();
        if (StrUtil.isBlank(namespace)) {
            throw new ConfigEmptyException("Web server failed to start. The dynamic thread pool namespace is empty.", "Please check whether the [spring.dynamic.thread-pool.namespace] configuration is empty or an empty string.");
        }

        String appId = properties.getAppId();
        if (StrUtil.isBlank(appId)) {
            throw new ConfigEmptyException("Web server failed to start. The dynamic thread pool item id is empty.", "Please check whether the [spring.dynamic.thread-pool.item-id] configuration is empty or an empty string.");
        }

        String serverAddr = properties.getServerAddr();
        if (StrUtil.isBlank(serverAddr)) {
            throw new ConfigEmptyException("Web server failed to start. The dynamic thread pool server addr is empty.", "Please check whether the [spring.dynamic.thread-pool.server-addr] configuration is empty or an empty string.");
        }

        String applicationName = environment.getProperty("spring.application.name");
        if (StrUtil.isBlank(applicationName)) {
            throw new ConfigEmptyException("Web server failed to start. The dynamic thread pool application name is empty.", "Please check whether the [spring.application.name] configuration is empty or an empty string.");
        }

        return new BeforeCheck();
    }

    public class BeforeCheck {

    }

}
