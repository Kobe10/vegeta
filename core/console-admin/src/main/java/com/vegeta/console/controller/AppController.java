package com.vegeta.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.console.service.AppService;
import com.vegeta.datasource.model.AppInfo;
import com.vegeta.datasource.server.model.condition.AppQueryCondition;
import com.vegeta.datasource.server.model.dto.AppSaveDto;
import com.vegeta.datasource.server.model.dto.AppUpdateDto;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/app")
public class AppController {

    private final AppService appService;

    /**
     * 分页查询应用
     *
     * @param appQueryCondition 查询条件
     * @return com.vegeta.global.http.result.base.Result<com.baomidou.mybatisplus.core.metadata.IPage < com.vegeta.datasource.model.AppInfo>>
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @PostMapping("/query/page")
    public Result<IPage<AppInfo>> queryAppPage(@RequestBody AppQueryCondition appQueryCondition) {
        return Results.success(appService.queryAppList(appQueryCondition));
    }

    /**
     * 根据appId 查询
     *
     * @param tenantId 部门id
     * @param appId    appId
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @GetMapping("/query/{tenantId}/{appId}")
    public Result queryAppById(@PathVariable("tenantId") String tenantId, @PathVariable("appId") String appId) {
        return Results.success(appService.queryAppById(tenantId, appId));
    }

    /**
     * 新增应用
     *
     * @param appSaveDto
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @PostMapping("/save")
    public Result saveItem(@RequestBody AppSaveDto appSaveDto) {
        appService.saveApp(appSaveDto);
        return Results.success();
    }

    /**
     * 更新应用信息
     *
     * @param appUpdateDto
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @PostMapping("/update")
    public Result updateItem(@RequestBody AppUpdateDto appUpdateDto) {
        appService.updateApp(appUpdateDto);
        return Results.success();
    }

    /**
     * 删除应用
     *
     * @param tenantId 部门
     * @param
     * @return com.vegeta.global.http.result.base.Result
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    @DeleteMapping("/delete/{tenantId}/{appId}")
    public Result deleteItem(@PathVariable("tenantId") String tenantId, @PathVariable("appId") String appId) {
        appService.deleteApp(tenantId, appId);
        return Results.success();
    }
}
