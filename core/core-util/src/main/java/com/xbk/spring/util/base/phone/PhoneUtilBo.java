package com.xbk.spring.util.base.phone;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PhoneUtilBo {

    /**
     * 省会
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 运营商
     */
    private String carrier;
}
