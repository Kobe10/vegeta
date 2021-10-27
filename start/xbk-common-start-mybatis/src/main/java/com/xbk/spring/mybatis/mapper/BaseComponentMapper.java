package com.xbk.spring.mybatis.mapper;

import com.xbk.spring.mybatis.model.BaseEntityOnlyId;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BaseComponentMapper<T extends BaseEntityOnlyId> extends
        InsertComponentMapper<T> {
}
