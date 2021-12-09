package com.vegeta.datasource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vegeta.logrecord.model.LogRecordInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * Log record mapper.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
@Mapper
public interface LogRecordMapper extends BaseMapper<LogRecordInfo> {
}