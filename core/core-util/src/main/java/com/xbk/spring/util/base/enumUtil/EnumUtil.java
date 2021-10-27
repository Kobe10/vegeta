package com.xbk.spring.util.base.enumUtil;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class EnumUtil {

    /**
     * 枚举 code 转换成 名称
     *
     * @param code  code
     * @param clazz 枚举实体
     * @return 名称 默认值 StringUtils.EMPTY
     */
    public static <T extends EnumUtilInterface> String valueOf(int code, Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return StringUtils.EMPTY;
        }
        Optional<String> optional = Arrays.stream(clazz.getEnumConstants())
                .filter(constant -> constant.getKey() == code)
                .map(T::getValue)
                .findAny();
        return optional.orElse(StringUtils.EMPTY);
    }

    /**
     * 枚举 名称 转换成 code
     *
     * @param value 名称
     * @param clazz 枚举实体
     * @return code 默认值 NumberUtils.INTEGER_MINUS_ONE
     */
    public static <T extends EnumUtilInterface> int valueOf(String value, Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return NumberUtils.INTEGER_MINUS_ONE;
        }
        Optional<Integer> optional = Arrays.stream(clazz.getEnumConstants())
                .filter(constant -> constant.getValue().equals(value))
                .map(T::getKey)
                .findAny();
        return optional.orElse(NumberUtils.INTEGER_MINUS_ONE);
    }

    /**
     * 枚举 code 转换成 枚举
     *
     * @param code  code
     * @param clazz 枚举实体
     * @return code 默认值 null
     */
    public static <T extends EnumUtilInterface> T getEnum(int code, Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        Optional<T> optional = Arrays.stream(clazz.getEnumConstants())
                .filter(constant -> constant.getKey() == code)
                .findAny();
        return optional.orElse(null);
    }

    /**
     * 枚举 名称 转换成 枚举
     *
     * @param value 名称
     * @param clazz 枚举实体
     * @return code 默认值 null
     */
    public static <T extends EnumUtilInterface> T getEnum(String value, Class<T> clazz) {
        if (Objects.isNull(clazz)) {
            return null;
        }
        Optional<T> optional = Arrays.stream(clazz.getEnumConstants())
                .filter(constant -> constant.getValue().equals(value))
                .findAny();
        return optional.orElse(null);
    }

}

