package com.vegeta.datasource.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vegeta.global.model.PoolParameter;
import lombok.Data;

import java.util.Date;

/**
 * Config all info.
 *
 * @author chen.ma
 * @date 2021/6/20 15:14
 */
@Data
@TableName("thread_config")
public class ConfigAllInfo extends ThreadConfigInfo implements PoolParameter {
    /**
     * 创建人
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private String creator;

    /**
     * 更新人
     */
    @JSONField(serialize = false)
    @TableField(exist = false, fill = FieldFill.INSERT_UPDATE)
    private String updater;

    /**
     * 创建时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 删除标识
     */
    @TableLogic
    @JSONField(serialize = false)
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    @Override
    public Integer getIsAlarm() {
        return null;
    }

    @Override
    public Integer getCapacityAlarm() {
        return null;
    }

    @Override
    public Integer getLivenessAlarm() {
        return null;
    }
}
