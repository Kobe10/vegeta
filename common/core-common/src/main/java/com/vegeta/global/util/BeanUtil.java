package com.vegeta.global.util;


import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.*;

/**
 * Bean util
 *
 * @Author fuzhiqiang
 * @Date 2021/12/9
 */
public class BeanUtil {

    private BeanUtil() {

    }

    protected static Mapper BEAN_MAPPER_BUILDER;

    static {
        BEAN_MAPPER_BUILDER = DozerBeanMapperBuilder.buildDefault();
    }

    public static <T, S> T convert(S source, Class<T> clazz) {
        return Optional.ofNullable(source)
                .map(each -> BEAN_MAPPER_BUILDER.map(each, clazz))
                .orElse(null);
    }

    public static <T, S> List<T> convert(List<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    List<T> targetList = new ArrayList<T>(each.size());
                    each.stream()
                            .forEach(item -> targetList.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetList;
                })
                .orElse(null);
    }

    public static <T, S> Set<T> convert(Set<S> sources, Class<T> clazz) {
        return Optional.ofNullable(sources)
                .map(each -> {
                    Set<T> targetSize = new HashSet<T>(each.size());
                    each.stream()
                            .forEach(item -> targetSize.add(BEAN_MAPPER_BUILDER.map(item, clazz)));
                    return targetSize;
                })
                .orElse(null);
    }
}