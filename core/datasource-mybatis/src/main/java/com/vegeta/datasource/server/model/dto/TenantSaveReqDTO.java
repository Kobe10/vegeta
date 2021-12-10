package com.vegeta.datasource.server.model.dto;

import lombok.Data;

/**
 * Tenant save req dto.
 *
 * @author chen.ma
 * @date 2021/6/29 20:40
 */
@Data
public class TenantSaveReqDTO {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * tenantName
     */
    private String tenantName;

    /**
     * tenantDesc
     */
    private String tenantDesc;

    /**
     * owner
     */
    private String owner;

}
