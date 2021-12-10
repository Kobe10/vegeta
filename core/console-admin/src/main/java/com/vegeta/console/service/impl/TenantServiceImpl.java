package com.vegeta.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.vegeta.console.service.TenantService;
import com.vegeta.datasource.model.AppInfo;
import com.vegeta.datasource.model.TenantInfo;
import com.vegeta.datasource.server.mapper.AppInfoMapper;
import com.vegeta.datasource.server.mapper.TenantInfoMapper;
import com.vegeta.datasource.server.model.condition.AppQueryCondition;
import com.vegeta.datasource.server.model.condition.TenantQueryCondition;
import com.vegeta.datasource.server.model.dto.TenantSaveReqDTO;
import com.vegeta.datasource.server.model.dto.TenantUpdateReqDTO;
import com.vegeta.global.enums.DelEnum;
import com.vegeta.global.util.Assert;
import com.vegeta.global.util.BeanUtil;
import com.vegeta.logrecord.annotation.LogRecord;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p></p>
 * <p> xx
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月10日 14:26
 * @since 1.0
 */
@Service
public class TenantServiceImpl implements TenantService {
    @Resource
    private TenantInfoMapper tenantInfoMapper;

    @Resource
    private AppInfoMapper appInfoMapper;

    @Override
    public IPage<TenantInfo> queryTenantPage(TenantQueryCondition tenantQueryCondition) {
        LambdaQueryWrapper<TenantInfo> wrapper = Wrappers.lambdaQuery(TenantInfo.class)
                .eq(StringUtils.isNotEmpty(tenantQueryCondition.getTenantId()), TenantInfo::getTenantId, tenantQueryCondition.getTenantId())
                .eq(StringUtils.isNotEmpty(tenantQueryCondition.getTenantName()), TenantInfo::getTenantName, tenantQueryCondition.getTenantName())
                .eq(StringUtils.isNotEmpty(tenantQueryCondition.getOwner()), TenantInfo::getOwner, tenantQueryCondition.getOwner());

        return tenantInfoMapper.selectPage(tenantQueryCondition, wrapper);

    }

    @Override
    public TenantInfo getTenantByTenantId(String tenantId) {
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers
                .lambdaQuery(TenantInfo.class).eq(TenantInfo::getTenantId, tenantId);

        return tenantInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveTenant(TenantSaveReqDTO reqDTO) {
        LambdaQueryWrapper<TenantInfo> queryWrapper = Wrappers.lambdaQuery(TenantInfo.class)
                .eq(TenantInfo::getTenantId, reqDTO.getTenantId());

        TenantInfo existTenantInfo = tenantInfoMapper.selectOne(queryWrapper);
        Assert.isNull(existTenantInfo, "租户 ID 不允许重复.");

        TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
        int insertResult = tenantInfoMapper.insert(tenantInfo);

        boolean retBool = SqlHelper.retBool(insertResult);
        if (!retBool) {
            throw new RuntimeException("Save Error.");
        }
    }

    @Override
    @LogRecord(
            prefix = "item",
            bizNo = "{{#reqDTO.tenantId}}_{{#reqDTO.tenantName}}",
            category = "TENANT_UPDATE",
            success = "更新租户, ID :: {{#reqDTO.id}}, 租户名称由 :: {TENANT{#reqDTO.id}} -> {{#reqDTO.tenantName}}",
            detail = "{{#reqDTO.toString()}}"
    )
    public void updateTenant(TenantUpdateReqDTO reqDTO) {
        TenantInfo tenantInfo = BeanUtil.convert(reqDTO, TenantInfo.class);
        int updateResult = tenantInfoMapper.update(tenantInfo, Wrappers
                .lambdaUpdate(TenantInfo.class).eq(TenantInfo::getTenantId, reqDTO.getTenantId()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Update Error.");
        }
    }

    @Override
    public void deleteTenantById(String tenantId) {
        AppQueryCondition reqDTO = new AppQueryCondition();
        reqDTO.setTenantId(tenantId);
        LambdaQueryWrapper<AppInfo> wrapper = Wrappers.lambdaQuery(AppInfo.class)
                .eq(StringUtils.isNotEmpty(reqDTO.getAppId()), AppInfo::getAppId, reqDTO.getAppId())
                .eq(StringUtils.isNotEmpty(reqDTO.getTenantId()), AppInfo::getTenantId, reqDTO.getTenantId());

        List<AppInfo> itemList = appInfoMapper.selectList(wrapper);

        if (CollectionUtils.isNotEmpty(itemList)) {
            throw new RuntimeException("The line of business contains project references, and the deletion failed.");
        }

        int updateResult = tenantInfoMapper.update(new TenantInfo(),
                Wrappers.lambdaUpdate(TenantInfo.class)
                        .eq(TenantInfo::getTenantId, tenantId)
                        .set(TenantInfo::getDeleted, DelEnum.DELETE.getIntCode()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Delete error.");
        }
    }
}
