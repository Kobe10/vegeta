package com.vegeta.datasource.server.model.vo;

import com.vegeta.datasource.model.ConfigAllInfo;
import lombok.Data;

@Data
public class ThreadPoolInstanceInfo extends ConfigAllInfo {

    private static final long serialVersionUID = -3510887528692923273L;
    /**
     * clientAddress
     */
    private String clientAddress;

    /**
     * identify
     */
    private String identify;

    /**
     * clientBasePath
     */
    private String clientBasePath;

}
