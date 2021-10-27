package com.xbk.spring.mybatis.type;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;


@Slf4j
@NoArgsConstructor
public class XbkEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    /**
     * 当前枚举类型
     */
    private Class<E> type;

    /**
     * 是否是枚举扩展字段
     */
    private boolean isExpandField;

    public XbkEnumTypeHandler(Class<E> type) {
        if (Objects.isNull(type)) {
            throw new IllegalArgumentException("CrmEnumTypeHandler : Type argument cannot be null");
        }
        this.type = type;
        this.isExpandField = XbkMybatisBaseEnum.class.isAssignableFrom(type);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object dbValueByEnum = getDBValueByEnum(parameter);
        if (Objects.isNull(jdbcType)) {
            ps.setObject(i, dbValueByEnum);
        } else {
            ps.setObject(i, dbValueByEnum, jdbcType.TYPE_CODE);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object dbValue = rs.getObject(columnName);
        if (rs.wasNull()) {
            return null;
        }
        return getEnumByDbValue(dbValue);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object dbValue = rs.getObject(columnIndex);
        if (rs.wasNull()) {
            return null;
        }
        return getEnumByDbValue(dbValue);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object dbValue = cs.getObject(columnIndex);
        if (cs.wasNull()) {
            return null;
        }
        return getEnumByDbValue(dbValue);
    }

    /**
     * 通过 DB 的值获取实际映射的 枚举值
     *
     * @param dbValue 数据库值
     * @return 枚举值
     */
    protected E getEnumByDbValue(Object dbValue) {
        if (Objects.isNull(dbValue)) {
            return null;
        }
        if (isExpandField) {
            for (E e : type.getEnumConstants()) {
                if (dbValue.equals(((XbkMybatisBaseEnum) e).getDBValue())) {
                    return e;
                }
            }
            return null;
        } else {
            if (!(dbValue instanceof String)) {
                throw new IllegalArgumentException("CrmEnumTypeHandler : dbValue Type must be String");
            }
            return Enum.valueOf(type, (String) dbValue);
        }
    }

    /**
     * 通过 枚举获取数据库对应的信息
     *
     * @param e 枚举对象
     * @return 数据库数据
     */
    protected Object getDBValueByEnum(E e) {
        if (Objects.isNull(e)) {
            return null;
        }
        if (isExpandField) {
            XbkMybatisBaseEnum baseEnum = (XbkMybatisBaseEnum) e;
            return baseEnum.getDBValue();
        }
        return e.name();
    }
}
