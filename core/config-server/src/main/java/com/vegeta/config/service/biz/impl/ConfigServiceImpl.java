package com.vegeta.config.service.biz.impl;

import com.vegeta.config.service.biz.ConfigService;
import com.vegeta.datasource.model.ConfigAllInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
 * @date 2021年12月08日 15:26
 * @since 1.0
 */
@Slf4j
@Service
public class ConfigServiceImpl implements ConfigService {


    /**
     * Find config all info.
     *
     * @param tpId     tpId
     * @param itemId   itemId
     * @param tenantId tenantId
     * @return all config
     */
    @Override
    public ConfigAllInfo findConfigAllInfo(String tpId, String itemId, String tenantId) {
        return null;
    }

    /**
     * Insert or update.
     *
     * @param identify
     * @param configAllInfo
     */
    @Override
    public void insertOrUpdate(String identify, ConfigAllInfo configAllInfo) {

    }

    /**
     * Find config recent info.
     *
     * @param params
     * @return
     */
    @Override
    public ConfigAllInfo findConfigRecentInfo(String... params) {

        return null;
    }
}
