package com.vegeta.console.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolDelReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolQueryReqDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolRespDTO;
import com.vegeta.datasource.server.model.dto.threadpool.ThreadPoolSaveOrUpdateReqDTO;

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
public interface ThreadPoolService {

    IPage<ThreadPoolRespDTO> queryThreadPoolPage(ThreadPoolQueryReqDTO reqDTO);


    ThreadPoolRespDTO getThreadPool(ThreadPoolQueryReqDTO reqDTO);


    List<ThreadPoolRespDTO> getThreadPoolByItemId(String appId);


    void saveOrUpdateThreadPoolConfig(String identify, ThreadPoolSaveOrUpdateReqDTO reqDTO);


    void deletePool(ThreadPoolDelReqDTO reqDTO);

    /**
     * Alarm enable.
     *
     * @param id
     * @param isAlarm
     */
    void alarmEnable(String id, Integer isAlarm);

}
