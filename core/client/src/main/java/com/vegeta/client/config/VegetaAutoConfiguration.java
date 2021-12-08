package com.vegeta.client.config;

import cn.hutool.core.util.StrUtil;
import com.vegeta.client.annotion.VegetaMarkerConfiguration;
import com.vegeta.client.config.alarm.MessageAlarmConfig;
import com.vegeta.client.config.bootstrap.BootstrapProperties;
import com.vegeta.client.config.http.CorsConfig;
import com.vegeta.client.config.http.HttpClientConfig;
import com.vegeta.client.config.register.RegisterClientConfig;
import com.vegeta.client.core.ConfigService;
import com.vegeta.client.core.DynamicThreadPoolPostProcessor;
import com.vegeta.client.core.ThreadPoolConfigService;
import com.vegeta.client.core.ThreadPoolOperation;
import com.vegeta.client.handler.DynamicThreadPoolBannerHandler;
import com.vegeta.client.oapi.HttpAgent;
import com.vegeta.client.tool.inet.InetUtils;
import com.vegeta.global.config.ApplicationContextHolder;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
// 自动配置 按照顺序 配置下列类
// 只有当 VegetaMarkerConfiguration 注册之后  当前自动装配类才会生效
// VegetaMarkerConfiguration 又是依赖 EnableVegeta 注解的导入
// EnableVegeta 又是使用方的唯一入口；  这个自动 装配类就会扫描到所有的bean
@Configuration
@AllArgsConstructor
@ConditionalOnBean(VegetaMarkerConfiguration.Marker.class)
@EnableConfigurationProperties(BootstrapProperties.class)
@ImportAutoConfiguration({
        HttpClientConfig.class,
        // 当前客户端基本信息
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
     *
     * @return com.vegeta.client.handler.DynamicThreadPoolBannerHandler
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    @Bean
    public DynamicThreadPoolBannerHandler threadPoolBannerHandler() {
        return new DynamicThreadPoolBannerHandler(properties);
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ApplicationContextHolder vegetaApplicationContextHolder() {
        return new ApplicationContextHolder();
    }

    @Bean
    @SuppressWarnings("all")
    public ConfigService configService(HttpAgent httpAgent, InetUtils inetUtils) {
        // 获取本机固定ip
        String ip = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String port = environment.getProperty("server.port");
        // 192.168.0.105:8090
        String identification = StrUtil.builder(ip, ":", port).toString();
        // 初始化  clientWork
        return new ThreadPoolConfigService(httpAgent, identification);
    }

    // 初始化 ThreadPoolOperation bean
    @Bean
    public ThreadPoolOperation threadPoolOperation(ConfigService configService) {
        return new ThreadPoolOperation(properties, configService);
    }

    @Bean
    @SuppressWarnings("all")
    public DynamicThreadPoolPostProcessor threadPoolBeanPostProcessor(HttpAgent httpAgent, ThreadPoolOperation threadPoolOperation,
                                                                      ApplicationContextHolder hippo4JApplicationContextHolder) {
        return new DynamicThreadPoolPostProcessor(properties, httpAgent, threadPoolOperation);
    }

//    @Bean
//    public PoolRunStateController poolRunStateController() {
//        return new PoolRunStateController();
//    }
}