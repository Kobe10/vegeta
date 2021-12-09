package com.vegeta.datasource.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vegeta.datasource.model.TenantInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Tenant info mapper.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
@Mapper
public interface TenantInfoMapper extends BaseMapper<TenantInfo> {
}
