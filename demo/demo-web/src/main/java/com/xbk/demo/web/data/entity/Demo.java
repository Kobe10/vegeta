//package com.xbk.demo.web.data.entity;
//
////import com.fasterxml.jackson.annotation.JsonProperty;
//
//import com.xbk.spring.mybatis.model.BaseEntity;
//import lombok.Builder;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.Column;
//import javax.persistence.Table;
//import java.util.Date;
//
//@Data
//@EqualsAndHashCode(callSuper = true)
//@Table(name = "o_demo")
//@NoArgsConstructor
//public class Demo extends BaseEntity {
//
//    /**
//     * 测试 - 名称 当数据库字段与实体字段不一致时候
//     */
//    @Column(name = "demo_name")
//    private String name;
//
//    /**
//     * 示例 - 字符 当数据库字段与实体字段一致
//     */
//    @Column(name = "demo_num")
//    private NumberStatusEnum demoNum;
//
////    @JsonProperty
//    private String demoNumName(){
//        return demoNum.name();
//    }
//
//    @Builder
//    public Demo(Long id, Date createTime, Integer isDel, String name, NumberStatusEnum demoNum) {
//        super(id, createTime, null, isDel);
//        this.name = name;
//        this.demoNum = demoNum;
//    }
//}
