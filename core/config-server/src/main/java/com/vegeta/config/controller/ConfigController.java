package com.vegeta.config.controller;

import com.vegeta.config.service.ConfigCacheService;
import com.vegeta.config.service.biz.ConfigService;
import com.vegeta.config.toolkit.Md5ConfigUtil;
import com.vegeta.datasource.model.ConfigAllInfo;
import com.vegeta.datasource.model.ThreadConfig;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.util.ParamUtils;
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

    @GetMapping(value = "/detailConfigInfo")
    public Result<ThreadConfig> detailConfigInfo(@RequestParam("tpId") String tpId, @RequestParam("appId") String appId,
                                                 @RequestParam("namespace") String namespace,
                                                 @RequestParam(value = "instanceId", required = false) String instanceId) {
        // 校验租户id
        ParamUtils.checkTenant(tpId);
        // 获取配置信息
        ConfigAllInfo configAllInfo = configService.findConfigRecentInfo(tpId, appId, namespace, instanceId);
        return Results.success(configAllInfo);
    }

    /**
     * 发布配置信息
     *
     * @param identify 身份认证
     * @param config   配置信息 {@link ConfigAllInfo}
     * @return com.vegeta.global.http.result.base.Result<java.lang.Boolean>
     * @Author fuzhiqiang
     * @Date 2021/12/9
     */
    @PostMapping(value = "/publishConfig")
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

        // 做长轮训
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