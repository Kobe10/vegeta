package com.xbk.spring.mybatis.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum BaseStatusEnum {

    /**
     * 正常状态
     */
    NORMAL(0, "正常"),

    /**
     * 逻辑删除
     */
    DELETED(1, "逻辑删除");

    private Integer code;

    private String name;

    /**
     * 是否为正常数据
     *
     * @param code 需要判断的code
     * @return 正常数据返回 true
     */
    public static Boolean isNormal(Integer code) {
        return Objects.nonNull(code) && NORMAL.getCode().equals(code);
    }

}
