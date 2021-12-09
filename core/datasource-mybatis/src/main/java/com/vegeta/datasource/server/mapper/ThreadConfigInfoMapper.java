package com.vegeta.datasource.server.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vegeta.datasource.model.ConfigAllInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Config info mapper.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
@Mapper
public interface ThreadConfigInfoMapper extends BaseMapper<ConfigAllInfo> {
}
