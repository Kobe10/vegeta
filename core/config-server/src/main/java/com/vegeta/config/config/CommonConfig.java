package com.vegeta.config.config;

import com.vegeta.global.config.ApplicationContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Common config.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
@Configuration
public class CommonConfig {
    @Bean
    public ApplicationContextHolder simpleApplicationContextHolder() {
        return new ApplicationContextHolder();
    }
}
