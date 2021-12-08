package com.vegeta.client.spi;

import com.google.common.collect.Maps;
import com.vegeta.client.exception.ServiceLoaderInstantiationException;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Vegeta SPI Service Loader.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/4
 */
public class VegetaServiceLoader {

    private static final Map<Class<?>, Collection<Object>> SERVICES = Maps.newConcurrentMap();

    /**
     * 注册接口实现   加载并缓存
     *
     * @param serviceInterface 服务接口
     * @return void
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    public static void register(final Class<?> serviceInterface) {
        if (!SERVICES.containsKey(serviceInterface)) {
            SERVICES.put(serviceInterface, load(serviceInterface));
        }
    }

    /**
     * Load service
     *
     * <p>Load service by SPI and cache the classes for reducing cost when load second time.
     *
     * @param serviceInterface 服务接口
     * @return java.util.Collection<java.lang.Object>
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    private static <T> Collection<Object> load(final Class<T> serviceInterface) {
        Collection<Object> result = new LinkedList<>();
        // 加载当前接口的所有实现类   依次加入到内存中
        for (T each : ServiceLoader.load(serviceInterface)) {
            result.add(each);
        }
        return result;
    }

    /**
     * 获取单例服务的实现类  实现类的集合;
     *
     * @param service 接口
     * @return java.util.Collection<T>
     * @Author fuzhiqiang
     * @Date 2021/12/4
     */
    public static <T> Collection<T> getSingletonServiceInstances(final Class<T> service) {
        // 从注册的服务集合中获取单例    如果没有就返回默认值  空集合
        return (Collection<T>) SERVICES.getOrDefault(service, Collections.emptyList());
    }

    public static <T> Collection<T> newServiceInstances(final Class<T> service) {
        return SERVICES.containsKey(service) ? SERVICES.get(service).stream().map(each ->
                (T) newServiceInstance(each.getClass())).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static Object newServiceInstance(final Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (final InstantiationException | IllegalAccessException ex) {
            throw new ServiceLoaderInstantiationException(clazz, ex);
        }
    }
}