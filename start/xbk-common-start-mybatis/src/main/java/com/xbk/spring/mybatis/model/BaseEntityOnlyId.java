package com.xbk.spring.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
public class BaseEntityOnlyId implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date lastModifyTime;

    public BaseEntityOnlyId() {
    }

    public BaseEntityOnlyId(Date createTime, Date lastModifyTime) {
        this.createTime = createTime;
        this.lastModifyTime = lastModifyTime;
    }
}
