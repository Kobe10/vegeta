package com.vegeta.datasource.server.model.dto;

import lombok.Data;

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
 * @date 2021年12月10日 11:01
 * @since 1.0
 */
@Data
public class AppUpdateDto {

    /**
     * tenantId
     */
    private String tenantId;

    /**
     * itemId
     */
    private String appId;

    /**
     * itemName
     */
    private String appName;

    /**
     * itemDesc
     */
    private String appDesc;

    /**
     * owner
     */
    private String owner;
}
