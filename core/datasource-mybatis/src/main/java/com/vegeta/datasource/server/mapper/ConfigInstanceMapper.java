package com.vegeta.datasource.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vegeta.datasource.model.ConfigInstance;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant info mapper.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
@Mapper
public interface ConfigInstanceMapper extends BaseMapper<ConfigInstance> {
}
