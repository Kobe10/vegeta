package com.vegeta.meta.config;

import com.vegeta.meta.core.BaseInstanceRegistry;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Registry configuration.    参考eureka
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 */
@Configuration
@AllArgsConstructor
public class RegistryConfiguration {

    private final BaseInstanceRegistry baseInstanceRegistry;

    // 启动初始化
    @PostConstruct
    public void registryInit() {
        baseInstanceRegistry.postInit();
    }
}
