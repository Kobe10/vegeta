package com.vegeta.config.service.biz;

import cn.hippo4j.config.model.ConfigAllInfo;
import com.vegeta.datasource.model.ConfigAllInfo;

/**
 * Config service.
 *
 * @author chen.ma
 * @date 2021/6/20 15:18
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

}
