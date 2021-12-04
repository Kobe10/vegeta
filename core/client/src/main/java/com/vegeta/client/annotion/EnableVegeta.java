package com.vegeta.client.annotion;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 激活动态线程池相关配置的注解。   效仿eureka的自动装配
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
// 导入VegetaMarkerConfiguration bean (因为client 的自动装配配置类依赖VegetaMarkerConfiguration 类)
@Import(VegetaMarkerConfiguration.class)
public @interface EnableVegeta {

}
