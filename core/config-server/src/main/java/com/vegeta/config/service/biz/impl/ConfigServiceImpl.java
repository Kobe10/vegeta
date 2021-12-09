package com.vegeta.config.service.biz.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.vegeta.config.model.event.LocalDataChangeEvent;
import com.vegeta.config.service.ConfigChangePublisher;
import com.vegeta.config.service.biz.ConfigService;
import com.vegeta.config.toolkit.Md5ConfigUtil;
import com.vegeta.datasource.model.ConfigAllInfo;
import com.vegeta.datasource.model.ConfigInstance;
import com.vegeta.datasource.model.ThreadConfig;
import com.vegeta.datasource.server.mapper.ConfigInstanceMapper;
import com.vegeta.datasource.server.mapper.ThreadConfigInfoMapper;
import com.vegeta.global.config.ApplicationContextHolder;
import com.vegeta.global.util.BeanUtil;
import com.vegeta.global.util.ConditionUtil;
import com.vegeta.global.util.ContentUtil;
import com.vegeta.logrecord.annotation.LogRecord;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p></p>
 * <p> Config service
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月08日 15:26
 * @since 1.0
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {

    @Resource
    private ThreadConfigInfoMapper threadConfigInfoMapper;

    @Resource
    private ConfigInstanceMapper configInstanceMapper;

    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String appId, String tenantId) {
        LambdaQueryWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaQuery(ConfigAllInfo.class)
                .eq(StringUtils.isNotBlank(tpId), ConfigAllInfo::getThreadPoolId, tpId)
                .eq(StringUtils.isNotBlank(appId), ConfigAllInfo::getAppId, appId)
                .eq(StringUtils.isNotBlank(tenantId), ConfigAllInfo::getTenantId, tenantId);

        return threadConfigInfoMapper.selectOne(wrapper);
    }

    /**
     * 新增或者更新线程池配置信息
     *
     * @param identify   身份标识
     * @param configInfo {@link ConfigAllInfo}
     * @Author fuzhiqiang
     * @Date 2021/12/9
     */
    @Override
    public void insertOrUpdate(String identify, ConfigAllInfo configInfo) {
        // 查询唯一配置信息
        ConfigAllInfo existConfig = threadConfigInfoMapper.selectOne(new QueryWrapper<ConfigAllInfo>().lambda().eq(ConfigAllInfo::getTenantId, configInfo.getTenantId()).eq(ThreadConfig::getAppId, configInfo.getAppId()).eq(ConfigAllInfo::getDeleted, 0).eq(ThreadConfig::getThreadPoolId, configInfo.getThreadPoolId()));

        ConfigServiceImpl configService = ApplicationContextHolder.getBean(this.getClass());
        configInfo.setCapacity(getQueueCapacityByType(configInfo));

        try {
            ConditionUtil.condition(existConfig == null,
                    // 新增配置
                    () -> configService.addConfigInfo(configInfo),
                    // 更新配置信息  保存之前的配置信息(备份)
                    () -> configService.updateConfigInfo(identify, configInfo));
        } catch (Exception ex) {
            updateConfigInfo(identify, configInfo);
        }
        // 发布 LocalDataChangeEvent 事件
        ConfigChangePublisher.notifyConfigChange(new LocalDataChangeEvent(identify, ContentUtil.getGroupKey(configInfo)));
    }

    /**
     * Find config recent info.
     *
     * @param params
     * @return
     */
    @Override
    public ConfigAllInfo findConfigRecentInfo(String... params) {

        ConfigAllInfo resultConfig;
        ConfigAllInfo configInstance = null;
        // 查询最新的实例信息
        LambdaQueryWrapper<ConfigInstance> instanceQueryWrapper = Wrappers.lambdaQuery(ConfigInstance.class).eq(ConfigInstance::getInstanceId, params[3]).orderByDesc(ConfigInstance::getCreateTime).last("LIMIT 1");

        ConfigInstance instanceInfo = configInstanceMapper.selectOne(instanceQueryWrapper);
        if (Objects.nonNull(instanceInfo)) {
            String content = instanceInfo.getContent();
            configInstance = JSON.parseObject(content, ConfigAllInfo.class);
            configInstance.setContent(content);
            configInstance.setCreateTime(instanceInfo.getCreateTime());
            configInstance.setMd5(Md5ConfigUtil.getTpContentMd5(configInstance));
        }
        // 查询线程池的实例配置信息
        ConfigAllInfo configAllInfo = findConfigAllInfo(params[0], params[1], params[2]);
        if (Objects.nonNull(configAllInfo) && Objects.isNull(configInstance)) {
            resultConfig = configAllInfo;
        } else if (Objects.isNull(configAllInfo) && Objects.nonNull(configInstance)) {
            resultConfig = configInstance;
        } else {
            return null;
        }
        return resultConfig;
    }

    /**
     * 保存配置信息
     *
     * @param config {@link ConfigAllInfo}
     * @return java.lang.Long 主键
     * @Author fuzhiqiang
     * @Date 2021/12/9
     */
    public Long addConfigInfo(ConfigAllInfo config) {
        // 构造基本参数
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5ConfigUtil.getTpContentMd5(config));
        try {
            // 保证数据库操作成功
            if (SqlHelper.retBool(threadConfigInfoMapper.insert(config))) {
                return config.getId();
            }
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    /**
     * 更新配置信息
     *
     * @param identify 身份标识
     * @param config   {@link ConfigAllInfo}
     * @Author fuzhiqiang
     * @Date 2021/12/9
     */
    @LogRecord(bizNo = "{{#config.appId}}_{{#config.threadPoolId}}", category = "THREAD_POOL_UPDATE", success = "核心线程: {{#config.coreSize}}, 最大线程: {{#config.maxSize}}, 队列类型: {{#config.queueType}}, " + "队列容量: {{#config.capacity}}, 拒绝策略: {{#config.rejectedType}}", detail = "{{#config.toString()}}")
    public void updateConfigInfo(String identify, ConfigAllInfo config) {
        LambdaUpdateWrapper<ConfigAllInfo> wrapper = Wrappers.lambdaUpdate(ConfigAllInfo.class).eq(ConfigAllInfo::getThreadPoolId, config.getThreadPoolId()).eq(ConfigAllInfo::getAppId, config.getAppId()).eq(ConfigAllInfo::getTenantId, config.getTenantId());

        config.setCreator("system");
        config.setContent(ContentUtil.getPoolContent(config));
        config.setMd5(Md5ConfigUtil.getTpContentMd5(config));

        try {
            // 创建线程池配置实例临时配置, 也可以当作历史配置, 不过针对的是单节点
            if (StringUtils.isNotBlank(identify)) {
                ConfigInstance instanceInfo = BeanUtil.convert(config, ConfigInstance.class);
                instanceInfo.setInstanceId(identify);
                configInstanceMapper.insert(instanceInfo);
                return;
            }

            threadConfigInfoMapper.update(config, wrapper);
        } catch (Exception ex) {
            log.error("[db-error] message :: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    /**
     * 根据队列类型获取队列大小.
     * <p>
     * 不支持设置队列大小 {@link SynchronousQueue} {@link LinkedTransferQueue}
     *
     * @param config {@link ConfigAllInfo}
     */
    private Integer getQueueCapacityByType(ConfigAllInfo config) {
        int queueCapacity;
        if (Objects.equals(config.getQueueType(), 5)) {
            queueCapacity = Integer.MAX_VALUE;
        } else {
            queueCapacity = config.getCapacity();
        }

        List<Integer> queueTypes = Stream.of(1, 2, 3, 6, 9).collect(Collectors.toList());
        boolean setDefaultFlag = queueTypes.contains(config.getQueueType()) && (config.getCapacity() == null || Objects.equals(config.getCapacity(), 0));
        if (setDefaultFlag) {
            queueCapacity = 1024;
        }

        return queueCapacity;
    }
}
