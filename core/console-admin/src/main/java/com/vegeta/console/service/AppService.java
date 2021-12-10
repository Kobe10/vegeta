package com.vegeta.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.datasource.model.AppInfo;
import com.vegeta.datasource.server.model.condition.AppQueryCondition;
import com.vegeta.datasource.server.model.dto.AppSaveDto;
import com.vegeta.datasource.server.model.dto.AppUpdateDto;

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
public interface AppService {
    IPage<AppInfo> queryAppList(AppQueryCondition appQueryCondition);

    AppInfo queryAppById(String tenantId, String appId);

    void saveApp(AppSaveDto appSaveDto);

    void updateApp(AppUpdateDto appUpdateDto);

    void deleteApp(String tenantId, String appId);
}
