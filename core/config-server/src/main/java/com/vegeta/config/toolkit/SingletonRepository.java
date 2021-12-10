package com.vegeta.config.toolkit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 单例存储   存储service 单例实例
 *
 * @Author fuzhiqiang
 * @Date 2021/12/6
 */
public class SingletonRepository<T> {

    public SingletonRepository() {
        shared = new ConcurrentHashMap<>(1 << 16);
    }

    public T getSingleton(T obj) {
        T previous = shared.putIfAbsent(obj, obj);
        return (null == previous) ? obj : previous;
    }

    public int size() {
        return shared.size();
    }

    public void remove(Object obj) {
        shared.remove(obj);
    }

    private final ConcurrentHashMap<T, T> shared;

    public static class GroupIdCache {

        public static String getSingleton(String str) {
            return cache.getSingleton(str);
        }

        static SingletonRepository<String> cache = new SingletonRepository<>();
    }
}
