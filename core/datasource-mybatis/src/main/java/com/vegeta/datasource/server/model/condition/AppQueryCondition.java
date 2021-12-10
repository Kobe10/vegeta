package com.vegeta.datasource.server.model.condition;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
public class AppQueryCondition extends Page {
    private static final long serialVersionUID = 9174142884339493983L;
    /**
     * tenantId
     */
    private String tenantId;

    /**
     * appId
     */
    private String appId;

    /**
     * appName
     */
    private String appName;

    /**
     * owner
     */
    private String owner;
}
