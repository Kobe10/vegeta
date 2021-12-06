package com.vegeta.config.controller;

import com.vegeta.config.toolkit.Md5ConfigUtil;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Map;

/**
 * 服务端 提供外部接口
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.CONFIG_CONTROLLER_PATH)
public class ConfigController {

    private final ConfigService configService;

    private final ConfigServletInner configServletInner;

    @GetMapping
    public Result<ConfigInfoBase> detailConfigInfo(@RequestParam("tpId") String tpId, @RequestParam("itemId") String itemId, @RequestParam("namespace") String namespace, @RequestParam(value = "instanceId", required = false) String instanceId) {
        ConfigAllInfo configAllInfo = configService.findConfigRecentInfo(tpId, itemId, namespace, instanceId);
        return Results.success(configAllInfo);
    }

    @PostMapping
    public Result<Boolean> publishConfig(@RequestParam(value = "identify", required = false) String identify, @RequestBody ConfigAllInfo config) {
        configService.insertOrUpdate(identify, config);
        return Results.success(true);
    }

    /**
     * The client listens for configuration changes.
     * 注册监听器  (客户端定时校验线程池配置信息是否变更，触发注册监听器接口)
     *
     * @param request  请求体
     * @param response 返回流
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    @SneakyThrows
    @PostMapping("/listener")
    public void listener(HttpServletRequest request, HttpServletResponse response) {
        // 1、设置支持异步
        request.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        // 2、获取探针信息 (todo  这是个啥玩意)
        String probeModify = request.getParameter(Constants.LISTENING_CONFIGS);
        if (StringUtils.isEmpty(probeModify)) {
            throw new IllegalArgumentException("invalid probeModify");
        }

        probeModify = URLDecoder.decode(probeModify, Constants.ENCODE);

        Map<String, String> clientMd5Map;
        try {
            clientMd5Map = Md5ConfigUtil.getClientMd5Map(probeModify);
        } catch (Throwable e) {
            throw new IllegalArgumentException("invalid probeModify");
        }

        // do long-polling
        configServletInner.doPollingConfig(request, response, clientMd5Map, probeModify.length());
    }

    /**
     * 移除配置缓存信息
     *
     * @param bodyMap 请求体
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/6
     */
    @PostMapping("/remove/config/cache")
    public Result removeConfigCache(@RequestBody Map<String, String> bodyMap) {
        String groupKey = bodyMap.get(Constants.GROUP_KEY);
        if (StringUtils.isNotBlank(groupKey)) {
            ConfigCacheService.removeConfigCache(groupKey);
        }
        return Results.success();
    }
}