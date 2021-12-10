package com.vegeta.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.console.service.TenantService;
import com.vegeta.datasource.model.TenantInfo;
import com.vegeta.datasource.server.model.condition.TenantQueryCondition;
import com.vegeta.datasource.server.model.dto.TenantSaveReqDTO;
import com.vegeta.datasource.server.model.dto.TenantUpdateReqDTO;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p></p>
 * <p> 部门
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月10日 14:25
 * @since 1.0
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/tenant")
public class TenantController {
    @Resource
    private TenantService tenantService;

    @PostMapping("/query/page")
    public Result<IPage<TenantInfo>> queryNameSpacePage(@RequestBody TenantQueryCondition tenantQueryCondition) {
        IPage<TenantInfo> resultPage = tenantService.queryTenantPage(tenantQueryCondition);
        return Results.success(resultPage);
    }

    @GetMapping("/query/{tenantId}")
    public Result<TenantInfo> queryNameSpace(@PathVariable("tenantId") String tenantId) {
        return Results.success(tenantService.getTenantByTenantId(tenantId));
    }

    @PostMapping("/save")
    public Result<Boolean> saveNameSpace(@RequestBody TenantSaveReqDTO reqDTO) {
        tenantService.saveTenant(reqDTO);
        return Results.success(Boolean.TRUE);
    }

    @PostMapping("/update")
    public Result<Boolean> updateNameSpace(@RequestBody TenantUpdateReqDTO reqDTO) {
        tenantService.updateTenant(reqDTO);
        return Results.success(Boolean.TRUE);
    }

    @DeleteMapping("/delete/{tenantId}")
    public Result<Boolean> deleteNameSpace(@PathVariable("tenantId") String tenantId) {
        tenantService.deleteTenantById(tenantId);
        return Results.success(Boolean.TRUE);
    }
}
