package com.vegeta.global.annation;

import java.lang.annotation.*;

/**
 * 注解：标记一个方法不是线程安全的
 *
 * @author fuzhiqiang
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NotThreadSafe {

}
