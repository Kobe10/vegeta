package com.xbk.spring.web.config.log;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = LogConfig.PREFIX)
public class LogConfig {

    public static final String PREFIX = "spring.log";

    /**
     * tip 95 line
     */
    private Integer tip95Line = 150;

}       
