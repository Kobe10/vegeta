package com.vegeta.client.tool;

import com.vegeta.client.tool.inet.InetUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * Cloud common id util.
 *
 * @author chen.ma
 * @date 2021/8/6 21:02
 */
public class CommonIdUtil {

    private static final String SEPARATOR = ":";

    /**
     * 获取默认实例id 生成规则:   192.168.0.105:vegeta-example:8090
     *
     * @param resolver
     * @param inetUtils
     * @return java.lang.String
     * @Author fuzhiqiang
     * @Date 2021/11/24
     */
    @SneakyThrows
    public static String getDefaultInstanceId(PropertyResolver resolver, InetUtils inetUtils) {
        // example    -----    192.168.0.105:vegeta-example
        String namePart = getIpApplicationName(resolver, inetUtils);
        String indexPart = resolver.getProperty("spring.application.instance_id", Objects.requireNonNull(resolver.getProperty("server.port")));
        return combineParts(namePart, SEPARATOR, indexPart);
    }

    @SneakyThrows
    public static String getIpApplicationName(PropertyResolver resolver, InetUtils inetUtils) {
        String hostname = inetUtils.findFirstNonLoopbackHostInfo().getIpAddress();
        String appName = resolver.getProperty("spring.application.name");
        return combineParts(hostname, SEPARATOR, appName);
    }

    public static String combineParts(String firstPart, String separator,
                                      String secondPart) {
        StringBuilder combined = new StringBuilder();
        if (StringUtils.isEmpty(firstPart) && StringUtils.isEmpty(secondPart))
            return null;
        combined.append(firstPart).append(separator).append(secondPart);
        return combined.toString();
    }
}
