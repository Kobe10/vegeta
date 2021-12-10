package com.vegeta.logrecord.compare;

import java.util.List;

/**
 * 对象比对器.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
public interface Equator {

    /**
     * 判断两个对象是否相等.
     *
     * @param first
     * @param second
     * @return
     */
    boolean isEquals(Object first, Object second);

    /**
     * 获取两个对象不想等的属性.
     *
     * @param first
     * @param second
     * @return
     */
    List<FieldInfo> getDiffFields(Object first, Object second);
}