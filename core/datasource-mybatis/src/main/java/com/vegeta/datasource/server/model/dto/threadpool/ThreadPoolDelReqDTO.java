package com.vegeta.datasource.server.model.dto.threadpool;

import lombok.Data;


@Data
public class ThreadPoolDelReqDTO {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String appId;

    /**
     * threadPoolId
     */
    private String threadPoolId;

}
