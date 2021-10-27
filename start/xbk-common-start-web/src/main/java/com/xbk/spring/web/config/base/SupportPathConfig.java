package com.xbk.spring.web.config.base;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SupportPathConfig.PREFIX)
public class SupportPathConfig {

    public static final String PREFIX = "support-path";

    /**
     * 请求返回结果包装的路径，多个使用"，"分隔
     */
    private String response = "com.ziroom.crm";

    /**
     * swagger 支持路径
     */
    private String swagger = "com.ziroom.crm";
}       
