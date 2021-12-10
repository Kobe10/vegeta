package com.vegeta.config.service.biz;

import com.vegeta.datasource.model.ConfigAllInfo;

/**
 * Config service.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/8
 */
public interface ConfigService {

    /**
     * Find config all info.
     *
     * @param tpId     tpId
     * @param itemId   itemId
     * @param tenantId tenantId
     * @return all config
     */
    ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId);

    /**
     * Insert or update.
     *
     * @param identify
     * @param configAllInfo
     */
    void insertOrUpdate(String identify, ConfigAllInfo configAllInfo);

    /**
     * Find config recent info.
     *
     * @param params 多参数
     * @return com.vegeta.datasource.model.ConfigAllInfo
     * @Author fuzhiqiang
     * @Date 2021/12/8
     */
    ConfigAllInfo findConfigRecentInfo(String... params);
}
