package com.vegeta.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.datasource.server.model.condition.LogRecordQueryCondition;
import com.vegeta.logrecord.model.LogRecordInfo;

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
 * @date 2021年12月10日 14:09
 * @since 1.0
 */
public interface LogRecordBizService {


    IPage<LogRecordInfo> queryPage(LogRecordQueryCondition logRecordQueryCondition);
}
