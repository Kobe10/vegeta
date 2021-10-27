package com.xbk.spring.mybatis.mapper;
import com.xbk.spring.mybatis.model.BaseEntityOnlyId;
import com.xbk.spring.mybatis.provider.InsertCompeteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InsertComponentMapper<T extends BaseEntityOnlyId> {

    /**
     * 批量插入
     */
    @InsertProvider(type = InsertCompeteProvider.class, method = "insertAll")
    int insertAll(@Param("arg0") List<T> list);
}
