package com.vegeta.client.controller;

import com.vegeta.client.handler.ThreadPoolRunStateHandler;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.global.model.PoolRunStateInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 监控 线程池运行状态
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
@RestController
public class PoolRunStateController {

    @GetMapping("/run/state/{tpId}")
    public Result<PoolRunStateInfo> getPoolRunState(@PathVariable("tpId") String tpId) {
        PoolRunStateInfo poolRunState = ThreadPoolRunStateHandler.getPoolRunState(tpId);
        return Results.success(poolRunState);
    }
}