package com.vegeta.config.controller;

import com.vegeta.config.service.LongPollingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Config servlet inner.
 */
@Service
public class ConfigServletInner {

    // 初始化
    @Resource
    private LongPollingService longPollingService;

    /**
     * 长轮询配置信息
     *
     * @param request          请求
     * @param response         返回体
     * @param clientMd5Map     配置md5 map
     * @param probeRequestSize 探针大小
     * @return java.lang.String
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    public String doPollingConfig(HttpServletRequest request, HttpServletResponse response, Map<String, String> clientMd5Map, int probeRequestSize) {
        // Long polling.
        if (LongPollingService.isSupportLongPolling(request)) {
            longPollingService.addLongPollingClient(request, response, clientMd5Map, probeRequestSize);
            return HttpServletResponse.SC_OK + "";
        }
        return HttpServletResponse.SC_OK + "";
    }
}