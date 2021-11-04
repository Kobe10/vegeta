package com.xbk.spring.web.log;

import com.xbk.spring.web.config.log.LogConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@EnableConfigurationProperties(LogConfig.class)
public class LogFilterAutoConfig {

    @Bean
    public FilterRegistrationBean logFilter(@Autowired LogConfig logConfig) {
        final FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        final LogPrintFilter logFilter = new LogPrintFilter(logConfig);
        filterRegistrationBean.setFilter(logFilter);
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1);
        filterRegistrationBean.setName("logPrintFilter");
        return filterRegistrationBean;
    }

}
