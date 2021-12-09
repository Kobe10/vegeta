package com.vegeta.datasource.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

/**
 * <p></p>
 * <p> 实例配置表
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月09日 14:52
 * @since 1.0
 */
@Data
@TableName("inst_config")
public class ConfigInstance {

    @TableId(type = IdType.AUTO)
    /**
     * 主键id
     */ private Long id;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 项目id
     */
    private String appId;

    /**
     * 线程池id
     */
    private String threadPoolId;

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 线程池内容
     */
    private String content;

    /**
     * md5
     */
    private String md5;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * 删除标识
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
