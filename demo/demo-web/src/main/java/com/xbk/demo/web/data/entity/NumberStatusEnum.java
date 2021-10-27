//package com.xbk.demo.web.data.entity;
//
//
//
//import com.xbk.spring.mybatis.type.XbkMybatisBaseEnum;
//
//import com.xbk.spring.web.json.XbkWebInfoBaseEnum;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//
//@AllArgsConstructor
//@Getter
//public enum NumberStatusEnum implements XbkMybatisBaseEnum<Integer>, XbkWebInfoBaseEnum {
//    DEFAULT(-1),
//    FIRST(1),
//    SECOND(2);
//
//    /**
//     * code
//     */
//    private Integer code;
//
//    /**
//     * 获取与数据库匹配的 code
//     *
//     * @return 与数据库匹配的 code
//     */
//    @Override
//    public Integer getDBValue() {
//        return this.code;
//    }
//
//    /**
//     * 获取返回值中需要序列化的字段
//     *
//     * @return 序列化后的值
//     */
//    @Override
//    public String getWebInfoCode() {
//        return String.valueOf(this.code);
//    }
//
//    /**
//     * 反序列化默认值
//     *
//     * @return 反序列化默认值
//     */
//    @Override
//    public XbkWebInfoBaseEnum deserializeDefaultValue() {
//        return DEFAULT;
//    }
//}
