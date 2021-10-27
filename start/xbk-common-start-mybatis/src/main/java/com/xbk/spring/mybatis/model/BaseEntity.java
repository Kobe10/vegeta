package com.xbk.spring.mybatis.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEntity extends BaseEntityOnlyId implements Serializable {

    /**
     * 逻辑状态 - 数据状态
     */
    private Integer isDel;

    public BaseEntity() {
    }

    public BaseEntity(Long id, Date createTime, Date lastModifyTime) {
        super(id, createTime, lastModifyTime);
    }

    public BaseEntity(Long id, Date createTime, Date lastModifyTime, Integer isDel) {
        super(id, createTime, lastModifyTime);
        this.isDel = isDel;
    }
}
