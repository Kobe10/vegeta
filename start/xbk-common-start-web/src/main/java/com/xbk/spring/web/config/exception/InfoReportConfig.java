package com.xbk.spring.web.config.exception;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = InfoReportConfig.PREFIX)
public class InfoReportConfig {

    /**
     * 配置前缀
     */
    public static final String PREFIX = "spring.exception.alarm.report";

    /**
     * 上报时间，多个使用','分隔 支持 HH:SS:SS
     */
    private String reportTime;

}
