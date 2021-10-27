package com.xbk.core.global.system;

import lombok.experimental.UtilityClass;

import java.util.Objects;

/**
 * 获取系统属性
 */
@UtilityClass
public class SystemProperty {

    /**
     * 系统配置 主机名称
     */
    private final static String HOSTNAME = "HOSTNAME";

    /**
     * 系统配置 应用名称
     */
    private final static String APPLICATION_NAME = "APPLICATION_NAME";

    private static volatile String hoseName = "";
    private static volatile String applicationName = "";

    /**
     * 获取 主机名称
     */
    public static String getHostname() {
        if (isBlack(hoseName)) {
            synchronized (hoseName) {
                final String hostEnv = System.getenv(HOSTNAME);
                if (isBlack(hoseName) && !isBlack(hostEnv)) {
                    hoseName = hostEnv;
                }
            }
        }
        return hoseName;
    }

    /**
     * 获取当前计算机名称
     */
    public static String getApplicationName() {
        if (isBlack(applicationName)) {
            synchronized (applicationName) {
                final String applicationNameEnv = System.getenv(APPLICATION_NAME);
                if (isBlack(applicationName) && !isBlack(applicationNameEnv)) {
                    applicationName = applicationNameEnv;
                }
            }
        }
        return applicationName;
    }

    /**
     * 判断字符串是否为空
     */
    private static boolean isBlack(String str) {
        return Objects.isNull(str) || "".equals(str);
    }
}       
