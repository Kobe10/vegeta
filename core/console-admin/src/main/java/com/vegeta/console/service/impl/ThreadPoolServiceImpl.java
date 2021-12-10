package com.vegeta.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vegeta.config.service.biz.ConfigService;
import com.vegeta.console.service.ThreadPoolService;
import com.vegeta.datasource.model.ConfigAllInfo;
import com.vegeta.datasource.server.mapper.ThreadConfigInfoMapper;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolDelReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolQueryReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolRespDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolSaveOrUpdateReqDTO;
import com.vegeta.global.enums.DelEnum;
import com.vegeta.global.util.BeanUtil;
import com.vegeta.logrecord.annotation.LogRecord;
import lombok.AllArgsConstructor;
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
 * @date 2021年12月10日 14:56
 * @since 1.0
 */
@Service
@AllArgsConstructor
public class ThreadPoolServiceImpl implements ThreadPoolService {

    @Resource
    private ConfigService configService;

    @Resource
    private ThreadConfigInfoMapper configInfoMapper;

    @Override
    public IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(StringUtils.isNotBlank(reqDTO.getTenantId()), ConfigAllInfo::getTenantId, reqDTO.getTenantId())
                .eq(StringUtils.isNotBlank(reqDTO.getAppId()), ConfigAllInfo::getAppId, reqDTO.getAppId())
                .eq(StringUtils.isNotBlank(reqDTO.getThreadPoolId()), ConfigAllInfo::getThreadPoolId, reqDTO.getThreadPoolId())
                .eq(ConfigAllInfo::getDeleted, DelEnum.NORMAL)
                .orderByDesc(ConfigAllInfo::getCreateTime);

        return configInfoMapper.selectPage(reqDTO, wrapper).convert(each -> BeanUtil.convert(each, ThreadPoolRespDTO.class));
    }

    @Override
    public ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO) {
        ConfigAllInfo configAllInfo = configService.findConfigAllInfo(reqDTO.getThreadPoolId(), reqDTO.getAppId(), reqDTO.getTenantId());
        return BeanUtil.convert(configAllInfo, ThreadPoolRespDTO.class);
    }

    @Override
    public List<ThreadPoolRespDTO> getThreadPoolByItemId(String appId) {
        LambdaQueryWrapper<ConfigAllInfo> queryWrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(ConfigAllInfo::getAppId, appId);

        List<ConfigAllInfo> selectList = configInfoMapper.selectList(queryWrapper);
        return BeanUtil.convert(selectList, ThreadPoolRespDTO.class);
    }

    @Override
    public void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO) {
        configService.insertOrUpdate(identify, BeanUtil.convert(reqDTO, ConfigAllInfo.class));
    }

    @LogRecord(
            bizNo = "{{#reqDTO.itemId}}_{{#reqDTO.tpId}}",
            category = "THREAD_POOL_DELETE",
            success = "删除线程池: {{#reqDTO.tpId}}",
            detail = "{{#reqDTO.toString()}}"
    )
    @Override
    public void deletePool(ThreadPoolDelReqDTO reqDTO) {
        configInfoMapper.delete(
                Wrappers.lambdaUpdate(ConfigAllInfo.class)
                        .eq(ConfigAllInfo::getTenantId, reqDTO.getTenantId())
                        .eq(ConfigAllInfo::getAppId, reqDTO.getAppId())
                        .eq(ConfigAllInfo::getThreadPoolId, reqDTO.getThreadPoolId())
        );
    }

    @Override
    public void alarmEnable(String id, Integer isAlarm) {
        ConfigAllInfo configAllInfo = configInfoMapper.selectById(id);
//        configAllInfo.setIsAlarm(isAlarm);
        // TODO: 是否报警变更, 虽然通知了客户端, 但是并没有在客户端实时生效, 需要考虑一个好的场景思路
        configService.insertOrUpdate(null, configAllInfo);
    }
}
