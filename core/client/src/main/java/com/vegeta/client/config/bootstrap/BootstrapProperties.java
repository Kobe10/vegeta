package com.vegeta.client.config.bootstrap;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Bootstrap properties.
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
@Slf4j
@Getter
@Setter
@ConfigurationProperties(prefix = BootstrapProperties.PREFIX)
public class BootstrapProperties {

    public static final String PREFIX = "spring.vegeta.thread-pool";

    /**
     * serverAddr
     */
    private String serverAddr;

    /**
     * namespace
     */
    private String namespace;

    /**
     * appId
     */
    private String appId;

    /**
     * Enable banner
     */
    private boolean banner = true;

    /**
     * Alarm interval
     */
    private Long alarmInterval;

//    /**
//     * notifys
//     */
//    private List<NotifyConfig> notifys;
}
