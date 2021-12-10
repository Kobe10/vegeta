package com.vegeta.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.vegeta.console.service.AppService;
import com.vegeta.datasource.model.AppInfo;
import com.vegeta.datasource.model.ThreadConfig;
import com.vegeta.datasource.server.mapper.AppInfoMapper;
import com.vegeta.datasource.server.mapper.ThreadConfigInfoMapper;
import com.vegeta.datasource.server.model.condition.AppQueryCondition;
import com.vegeta.datasource.server.model.dto.AppSaveDto;
import com.vegeta.datasource.server.model.dto.AppUpdateDto;
import com.vegeta.global.enums.DelEnum;
import com.vegeta.global.util.Assert;
import com.vegeta.global.util.BeanUtil;
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
 * @date 2021年12月10日 10:58
 * @since 1.0
 */
@Service
public class AppServiceImpl implements AppService {

    @Resource
    private AppInfoMapper appInfoMapper;

    @Resource
    private ThreadConfigInfoMapper threadConfigInfoMapper;

    /**
     * 分页查询应用
     *
     * @param appQueryCondition 查询条件
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.vegeta.datasource.model.AppInfo>
     * @Author fuzhiqiang
     * @Date 2021/12/10
     */
    public IPage<AppInfo> queryAppList(AppQueryCondition appQueryCondition) {
        LambdaQueryWrapper<AppInfo> wrapper = Wrappers.lambdaQuery(AppInfo.class)
                .eq(StringUtils.isNotEmpty(appQueryCondition.getAppId()), AppInfo::getAppId, appQueryCondition.getAppId())
                .eq(StringUtils.isNotEmpty(appQueryCondition.getAppName()), AppInfo::getAppName, appQueryCondition.getAppName())
                .eq(StringUtils.isNotEmpty(appQueryCondition.getTenantId()), AppInfo::getTenantId, appQueryCondition.getTenantId())
                .eq(StringUtils.isNotEmpty(appQueryCondition.getOwner()), AppInfo::getOwner, appQueryCondition.getOwner());

        return appInfoMapper.selectPage(appQueryCondition, wrapper);
    }

    @Override
    public AppInfo queryAppById(String tenantId, String appId) {
        LambdaQueryWrapper<AppInfo> queryWrapper = Wrappers
                .lambdaQuery(AppInfo.class)
                .eq(AppInfo::getTenantId, tenantId)
                .eq(AppInfo::getAppId, appId);

        return appInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public void saveApp(AppSaveDto appSaveDto) {
        LambdaQueryWrapper<AppInfo> queryWrapper = Wrappers.lambdaQuery(AppInfo.class)
                .eq(AppInfo::getAppId, appSaveDto.getAppId());

        AppInfo existItemInfo = appInfoMapper.selectOne(queryWrapper);
        Assert.isNull(existItemInfo, "应用ID 不允许重复.");

        AppInfo appInfo = BeanUtil.convert(appSaveDto, AppInfo.class);
        int insertResult = appInfoMapper.insert(appInfo);

        boolean retBool = SqlHelper.retBool(insertResult);
        if (!retBool) {
            throw new RuntimeException("Save error");
        }
    }

    @Override
    public void updateApp(AppUpdateDto appUpdateDto) {
        AppInfo appInfo = BeanUtil.convert(appUpdateDto, AppInfo.class);
        int updateResult = appInfoMapper.update(appInfo,
                Wrappers.lambdaUpdate(AppInfo.class)
                        .eq(AppInfo::getTenantId, appUpdateDto.getTenantId())
                        .eq(AppInfo::getAppId, appUpdateDto.getAppId()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Update error.");
        }
    }

    @Override
    public void deleteApp(String tenantId, String appId) {
        List<ThreadConfig> itemList = threadConfigInfoMapper.getThreadPoolByAppId(appId);
        if (CollectionUtils.isNotEmpty(itemList)) {
            throw new RuntimeException("The project contains a thread pool reference, and the deletion failed.");
        }

        int updateResult = appInfoMapper.update(new AppInfo(),
                Wrappers.lambdaUpdate(AppInfo.class)
                        .eq(AppInfo::getTenantId, tenantId)
                        .eq(AppInfo::getAppId, appId)
                        .set(AppInfo::getDeleted, DelEnum.DELETE.getIntCode()));

        boolean retBool = SqlHelper.retBool(updateResult);
        if (!retBool) {
            throw new RuntimeException("Delete error.");
        }
    }


}
