package com.vegeta.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.datasource.model.TenantInfo;
import com.vegeta.datasource.server.model.condition.TenantQueryCondition;
import com.vegeta.datasource.server.model.dto.TenantSaveReqDTO;
import com.vegeta.datasource.server.model.dto.TenantUpdateReqDTO;

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
public interface TenantService {

    IPage<TenantInfo> queryTenantPage(TenantQueryCondition tenantQueryCondition);

    TenantInfo getTenantByTenantId(String tenantId);

    void saveTenant(TenantSaveReqDTO reqDTO);

    void updateTenant(TenantUpdateReqDTO reqDTO);

    void deleteTenantById(String tenantId);
}
