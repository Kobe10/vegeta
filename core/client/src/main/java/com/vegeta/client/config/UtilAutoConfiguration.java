package com.vegeta.client.config;

import com.vegeta.client.tool.inet.InetUtils;
import com.vegeta.client.tool.inet.InetUtilsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 工具类自动注入配置
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@EnableConfigurationProperties(InetUtilsProperties.class)
public class UtilAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public InetUtils vegetaInetUtils(InetUtilsProperties properties) {
        return new InetUtils(properties);
    }
}
