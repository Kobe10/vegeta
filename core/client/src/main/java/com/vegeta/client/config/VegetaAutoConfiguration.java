package com.vegeta.client.config;

import cn.hippo4j.common.config.ApplicationContextHolder;
import cn.hippo4j.starter.controller.PoolRunStateController;
import cn.hippo4j.starter.core.ConfigService;
import cn.hippo4j.starter.core.DynamicThreadPoolPostProcessor;
import cn.hippo4j.starter.core.ThreadPoolConfigService;
import cn.hippo4j.starter.core.ThreadPoolOperation;
import cn.hippo4j.starter.handler.DynamicThreadPoolBannerHandler;
import cn.hippo4j.starter.remote.HttpAgent;
import cn.hippo4j.starter.toolkit.inet.InetUtils;
import cn.hutool.core.util.StrUtil;
import com.vegeta.client.config.alarm.MessageAlarmConfig;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.config.http.CorsConfig;
import com.vegeta.client.config.http.HttpClientConfig;
import com.vegeta.client.config.register.RegisterClientConfig;
import com.vegeta.client.core.ConfigService;
import com.vegeta.client.handler.DynamicThreadPoolBannerHandler;
import com.vegeta.client.tool.thread.ThreadPoolOperation;
import com.vegeta.global.config.ApplicationContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 动态线程池自动注入配置
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Configuration
@AllArgsConstructor
//@ConditionalOnBean(MarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapProperties.class)
@ImportAutoConfiguration({
        HttpClientConfig.class,
        RegisterClientConfig.class,
        MessageAlarmConfig.class,
        UtilAutoConfiguration.class,
        CorsConfig.class
})
public class VegetaAutoConfiguration {

    private final BootstrapProperties properties;

    private final ConfigurableEnvironment environment;

    /**
     * banner 处理
     * @Author fuzhiqiang
     * @Date  2021/11/24
     * @return com.vegeta.client.handler.DynamicThreadPoolBannerHandler
     */
    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder hippo4JApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

//    @Bean
//    @SuppressWarnings("all")
//    public ConfigService configService(HttpAgent httpAgent, InetUtils hippo4JInetUtils) {
//        String ip = hippo4JInetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
//        String port = environment.getProperty("server.port");
//        String identification = StrUtil.builder(ip, ":", port).toString();
//        return new ThreadPoolConfigService(httpAgent, identification);
//    }

    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

//    @Bean
//    @SuppressWarnings("all")
//    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent, ThreadPoolOperation threadPoolOperation,
//                                                                      ApplicationContextHolder hippo4JApplicationContextHolder) {
//        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation);
//    }

//    @Bean
//    public PoolRunStateController poolRunStateController() {
//        return new PoolRunStateController();
//    }
}