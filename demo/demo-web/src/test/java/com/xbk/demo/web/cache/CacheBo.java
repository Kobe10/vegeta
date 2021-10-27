package com.xbk.demo.web.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: lijun
 * @Date: 2020/3/12 17:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CacheBo {
    //private String name;
    private String name1;
    private String  name2;
    private Integer age;

}
