package com.vegeta.datasource.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vegeta.datasource.model.AppInfo;
import com.vegeta.datasource.model.TenantInfo;
import com.vegeta.datasource.server.model.condition.AppQueryCondition;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * App info mapper.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
@Mapper
public interface AppInfoMapper extends BaseMapper<AppInfo> {
}
