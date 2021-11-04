package com.vegeta.global.enums;

/**
 * 逻辑删除类型 枚举
 *
 * @Author fuzhiqiang
 * @Date 2021/11/4
 */
public enum DelEnum {

    /**
     * Normal state
     */
    NORMAL("0"),

    /**
     * Deleted state
     */
    DELETE("1");

    private final String statusCode;

    DelEnum(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getCode() {
        return this.statusCode;
    }

    public Integer getIntCode() {
        return Integer.parseInt(this.statusCode);
    }

    @Override
    public String toString() {
        return statusCode;
    }

}
