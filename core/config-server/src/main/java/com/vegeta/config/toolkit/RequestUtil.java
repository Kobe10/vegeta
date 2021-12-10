package com.vegeta.config.toolkit;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.vegeta.global.consts.Constants.LONG_PULLING_CLIENT_IDENTIFICATION;

/**
 * 处理 request 工具类
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
public class RequestUtil {

    private static final String X_REAL_IP = "X-Real-IP";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";

    private static final String X_FORWARDED_FOR_SPLIT_SYMBOL = ",";

    public static final String CLIENT_APPNAME_HEADER = "Client-AppName";

    public static String getRemoteIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader(X_FORWARDED_FOR);
        if (!StringUtils.isEmpty(xForwardedFor)) {
            return xForwardedFor.split(X_FORWARDED_FOR_SPLIT_SYMBOL)[0].trim();
        }
        String nginxHeader = request.getHeader(X_REAL_IP);
        String ipPort = request.getHeader(LONG_PULLING_CLIENT_IDENTIFICATION);
        return StringUtils.isEmpty(nginxHeader) ? ipPort : nginxHeader;
    }
}