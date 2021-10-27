package com.xbk.demo.web.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Demo 视图结果
 */
@Data
@AllArgsConstructor
@Builder
public class DemoVO {

    /**
     * 名称
     */
    private String name;
}
