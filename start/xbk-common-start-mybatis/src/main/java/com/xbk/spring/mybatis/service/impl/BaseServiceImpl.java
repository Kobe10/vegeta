package com.xbk.spring.mybatis.service.impl;

import com.xbk.spring.mybatis.mapper.BaseComponentMapper;
import com.xbk.spring.mybatis.model.BaseEntityOnlyId;
import com.xbk.spring.mybatis.service.BaseService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import tk.mybatis.mapper.common.Mapper;

/**
 * @Author: lijun
 * @Date: 2020/2/7 20:31
 */
@NoArgsConstructor
public class BaseServiceImpl<T extends BaseEntityOnlyId> extends AbstractServiceImpl<T> implements BaseService<T> {

    @Autowired
    private Mapper<T> baseMapper;

    @Autowired
    private BaseComponentMapper<T> baseComponentMapper;

    @Override
    Mapper<T> baseMapper() {
        return baseMapper;
    }

    @Override
    BaseComponentMapper<T> baseComponentMapper() {
        return baseComponentMapper;
    }

}
