package com.vegeta.console.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.vegeta.console.service.LogRecordBizService;
import com.vegeta.datasource.server.model.condition.LogRecordQueryCondition;
import com.vegeta.global.consts.Constants;
import com.vegeta.global.http.result.base.Result;
import com.vegeta.global.http.result.base.Results;
import com.vegeta.logrecord.model.LogRecordInfo;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志记录
 *
 * @Author fuzhiqiang
 * @Date 2021/12/10
 */
@RestController
@AllArgsConstructor
@RequestMapping(Constants.BASE_PATH + "/log")
public class LogRecordController {

    private final LogRecordBizService logRecordBizService;

    @PostMapping("/query/page")
    public Result<IPage<LogRecordInfo>> queryPage(@RequestBody LogRecordQueryCondition logRecordQueryCondition) {
        return Results.success(logRecordBizService.queryPage(logRecordQueryCondition));
    }

}
