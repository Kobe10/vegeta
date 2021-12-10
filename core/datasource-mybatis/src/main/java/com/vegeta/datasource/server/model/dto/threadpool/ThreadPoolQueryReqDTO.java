package com.vegeta.datasource.server.model.dto.threadpool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * Thread pool query req dto.
 *
 * @author chen.ma
 * @date 2021/6/30 21:22
 */
@Data
public class ThreadPoolQueryReqDTO extends Page {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * appId
     */
    private String appId;

    /**
     * threadPoolId
     */
    private String threadPoolId;

}
