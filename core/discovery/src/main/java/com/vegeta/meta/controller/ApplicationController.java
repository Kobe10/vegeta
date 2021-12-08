package com.vegeta.meta.controller;

import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.http.result.exception.ErrorCodeEnum;
import com.vegeta.global.model.InstanceInfo;
import com.vegeta.meta.core.InstanceRegistry;
import com.vegeta.meta.core.Lease;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.vegeta.global.consts.Constants.BASE_PATH;

/**
 * Application controller.
 *
 * @author chen.ma
 * @date 2021/8/8 22:24
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BASE_PATH + "/apps")
public class ApplicationController {

    @NonNull
    private final InstanceRegistry instanceRegistry;

    @GetMapping("/{appName}")
    public Result applications(@PathVariable String appName) {
        List<Lease<InstanceInfo>> resultInstanceList = instanceRegistry.listInstance(appName);
        return Results.success(resultInstanceList);
    }

    /**
     * 注册实例
     *
     * @param instanceInfo {@link InstanceInfo}
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    @PostMapping("/register")
    public Result addInstance(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.register(instanceInfo);
        return Results.success();
    }

    @PostMapping("/renew")
    public Result renew(@RequestBody InstanceInfo.InstanceRenew instanceRenew) {
        boolean isSuccess = instanceRegistry.renew(instanceRenew);
        if (!isSuccess) {
            log.warn("Not Found (Renew) :: {} - {}", instanceRenew.getAppName(), instanceRenew.getInstanceId());
            return Results.failure(ErrorCodeEnum.NOT_FOUND);
        }
        return Results.success();
    }

    @PostMapping("/remove")
    public Result remove(@RequestBody InstanceInfo instanceInfo) {
        instanceRegistry.remove(instanceInfo);
        return Results.success();
    }
}