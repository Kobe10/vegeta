package com.vegeta.datasource.model;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * <p></p>
 * <p> 客户端 应用表
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月09日 11:25
 * @since 1.0
 */
@Data
@Table(name = "app")
public class AppInfo implements Serializable {

    private static final long serialVersionUID = 2757788532508435969L;

    @TableId(type = IdType.AUTO)
    private Integer id;


    /**
     * appid
     */
    private String appId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * app名称
     */
    private String appName;

    /**
     * app介绍
     */
    private String appDesc;

    /**
     * 负责人
     */
    private String owner;

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