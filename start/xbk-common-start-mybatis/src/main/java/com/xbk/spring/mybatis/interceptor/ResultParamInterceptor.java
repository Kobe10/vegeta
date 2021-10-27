package com.xbk.spring.mybatis.interceptor;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;

@Data
@NoArgsConstructor
@Intercepts({
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
public class ResultParamInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];

        if (ms.getResource().contains(Common.PREFIX_XML)) {
            return invocation.proceed();
        }
        if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
            return invocation.proceed();
        }
        Object parameter = args[1];
        BoundSql boundSql = args.length == 4 ? ms.getBoundSql(parameter) : (BoundSql) args[5];
        if (Common.SELECT_ID.equals(boundSql.getSql())) {
            return invocation.proceed();
        }
        ResultMap resultMap = ms.getResultMaps().iterator().next();
        if (!CollectionUtils.isEmpty(resultMap.getResultMappings())) {
            return invocation.proceed();
        }
        Class<?> type = resultMap.getType();
        if (ClassUtils.isAssignable(type, Collection.class) ||
                ClassUtils.isAssignable(type, Character.class) ||
                ClassUtils.isAssignable(type, Integer.class) ||
                ClassUtils.isAssignable(type, Long.class) ||
                ClassUtils.isAssignable(type, Boolean.class)) {
            return invocation.proceed();
        }
        EntityTable entityTable = EntityHelper.getEntityTable(type);
        //修改 resultMaps
        Field field = ReflectionUtils.findField(MappedStatement.class, Common.FIELD_RESULT_MAP);
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, ms, Collections.singletonList(entityTable.getResultMap(ms.getConfiguration())));
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
    }
}
