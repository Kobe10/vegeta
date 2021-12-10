package com.vegeta.logrecord.aop;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vegeta.logrecord.annotation.LogRecord;
import com.vegeta.logrecord.context.LogRecordContext;
import com.vegeta.logrecord.model.LogRecordInfo;
import com.vegeta.logrecord.model.LogRecordOps;
import com.vegeta.logrecord.model.MethodExecuteResult;
import com.vegeta.logrecord.parse.LogRecordOperationSource;
import com.vegeta.logrecord.parse.LogRecordValueParser;
import com.vegeta.logrecord.service.LogRecordService;
import com.vegeta.logrecord.service.OperatorGetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 日志记录切面.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class LogRecordAspect {

    private final LogRecordService bizLogService;

    private final LogRecordValueParser logRecordValueParser;

    private final OperatorGetService operatorGetService;

    private final LogRecordOperationSource logRecordOperationSource;

    private final ConfigurableEnvironment environment;

    @Around("@annotation(logRecord)")
    public Object logRecord(ProceedingJoinPoint joinPoint, LogRecord logRecord) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object target = joinPoint.getTarget();
        Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
        Object[] args = joinPoint.getArgs();

        LogRecordContext.putEmptySpan();

        Object result = null;
        Collection<LogRecordOps> operations = Lists.newArrayList();
        Map<String, String> functionNameAndReturnMap = Maps.newHashMap();
        MethodExecuteResult methodExecuteResult = new MethodExecuteResult(true);

        try {
            operations = logRecordOperationSource.computeLogRecordOperations(method, targetClass);
            List<String> spElTemplates = getBeforeExecuteFunctionTemplate(operations);
            functionNameAndReturnMap = logRecordValueParser.processBeforeExecuteFunctionTemplate(spElTemplates, targetClass, method, args);

        } catch (Exception ex) {
            log.error("Log record parse before function exception.", ex);
        }

        try {
            result = joinPoint.proceed();
        } catch (Exception ex) {
            methodExecuteResult = new MethodExecuteResult(false, ex, ex.getMessage());
        }

        try {
            if (!CollectionUtils.isEmpty(operations)) {
                recordExecute(result, method, args, operations, targetClass,
                        methodExecuteResult.isSuccess(), methodExecuteResult.getErrorMsg(), functionNameAndReturnMap);
            }
        } catch (Exception ex) {
            log.error("Log record parse exception.", ex);
        } finally {
            LogRecordContext.clear();
        }

        if (methodExecuteResult.getThrowable() != null) {
            throw methodExecuteResult.getThrowable();
        }

        return result;

    }

    private List<String> getBeforeExecuteFunctionTemplate(Collection<LogRecordOps> operations) {
        List<String> spElTemplates = new ArrayList();
        for (LogRecordOps operation : operations) {
            // 执行之前的函数
            List<String> templates = getSpElTemplates(operation, operation.getSuccessLogTemplate());
            if (!CollectionUtils.isEmpty(templates)) {
                spElTemplates.addAll(templates);
            }
        }

        return spElTemplates;
    }

    private List<String> getSpElTemplates(LogRecordOps operation, String action) {
        List<String> spElTemplates = Lists.newArrayList(operation.getBizKey(), operation.getBizNo(), action, operation.getDetail());

        if (!StringUtils.isEmpty(operation.getCondition())) {
            spElTemplates.add(operation.getCondition());
        }

        return spElTemplates;
    }

    /**
     * 记录日志.
     *
     * @param ret
     * @param method
     * @param args
     * @param operations
     * @param targetClass
     * @param success
     * @param errorMsg
     * @param functionNameAndReturnMap
     */
    private void recordExecute(Object ret, Method method, Object[] args, Collection<LogRecordOps> operations,
                               Class<?> targetClass, boolean success, String errorMsg, Map<String, String> functionNameAndReturnMap) {
        for (LogRecordOps operation : operations) {
            try {
                String action = getActionContent(success, operation);
                if (StringUtils.isEmpty(action)) {
                    // 没有日志内容则忽略
                    continue;
                }
                // 获取需要解析的表达式
                List<String> spElTemplates = getSpElTemplates(operation, action);
                String operatorIdFromService = getOperatorIdFromServiceAndPutTemplate(operation, spElTemplates);

                Map<String, String> expressionValues = logRecordValueParser.processTemplate(spElTemplates, ret, targetClass, method, args, errorMsg, functionNameAndReturnMap);
                if (logConditionPassed(operation.getCondition(), expressionValues)) {
                    LogRecordInfo logRecordInfo = LogRecordInfo.builder()
                            .tenant(environment.getProperty("tenant"))
                            .bizKey(expressionValues.get(operation.getBizKey()))
                            .bizNo(expressionValues.get(operation.getBizNo()))
                            .operator(getRealOperatorId(operation, operatorIdFromService, expressionValues))
                            .category(operation.getCategory())
                            .detail(expressionValues.get(operation.getDetail()))
                            .action(expressionValues.get(action))
                            .createTime(new Date())
                            .build();

                    // 如果 action 为空, 不记录日志
                    if (StringUtils.isEmpty(logRecordInfo.getAction())) {
                        continue;
                    }
                    // save log 需要新开事务, 失败日志不能因为事务回滚而丢失
                    Preconditions.checkNotNull(bizLogService, "bizLogService not init");
                    bizLogService.record(logRecordInfo);
                }
            } catch (Exception t) {
                log.error("log record execute exception", t);
            }
        }
    }

    private String getActionContent(boolean success, LogRecordOps operation) {
        if (success) {
            return operation.getSuccessLogTemplate();
        }

        return operation.getFailLogTemplate();
    }

    private String getOperatorIdFromServiceAndPutTemplate(LogRecordOps operation, List<String> spElTemplates) {
        String realOperatorId = "";
        if (StringUtils.isEmpty(operation.getOperatorId())) {
            realOperatorId = operatorGetService.getUser().getOperatorId();
            if (StringUtils.isEmpty(realOperatorId)) {
                throw new IllegalArgumentException("LogRecord operator is null");
            }
        } else {
            spElTemplates.add(operation.getOperatorId());
        }

        return realOperatorId;
    }

    private boolean logConditionPassed(String condition, Map<String, String> expressionValues) {
        return StringUtils.isEmpty(condition) || StringUtils.endsWithIgnoreCase(expressionValues.get(condition), "true");
    }

    private String getRealOperatorId(LogRecordOps operation, String operatorIdFromService, Map<String, String> expressionValues) {
        return !StringUtils.isEmpty(operatorIdFromService) ? operatorIdFromService : expressionValues.get(operation.getOperatorId());
    }
}
