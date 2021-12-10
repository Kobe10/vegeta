package com.vegeta.console.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.vegeta.console.service.LogRecordBizService;
import com.vegeta.datasource.server.mapper.LogRecordMapper;
import com.vegeta.datasource.server.model.condition.LogRecordQueryCondition;
import com.vegeta.global.util.BeanUtil;
import com.vegeta.logrecord.model.LogRecordInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p></p>
 * <p> xx
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @version 1.0
 * @date 2021年12月10日 14:09
 * @since 1.0
 */
@Service
public class LogRecordBizServiceImpl implements LogRecordBizService {

    @Resource
    private LogRecordMapper logRecordMapper;

    @Override
    public IPage<LogRecordInfo> queryPage(LogRecordQueryCondition logRecordQueryCondition) {
        LambdaQueryWrapper<LogRecordInfo> queryWrapper = Wrappers.lambdaQuery(LogRecordInfo.class)
                .eq(StringUtils.isNotBlank(logRecordQueryCondition.getBizNo()), LogRecordInfo::getBizNo, logRecordQueryCondition.getBizNo())
                .eq(StringUtils.isNotBlank(logRecordQueryCondition.getCategory()), LogRecordInfo::getCategory, logRecordQueryCondition.getCategory())
                .eq(StringUtils.isNotBlank(logRecordQueryCondition.getOperator()), LogRecordInfo::getOperator, logRecordQueryCondition.getOperator())
                .orderByDesc(LogRecordInfo::getCreateTime);
        return logRecordMapper.selectPage(logRecordQueryCondition, queryWrapper);

    }
}
