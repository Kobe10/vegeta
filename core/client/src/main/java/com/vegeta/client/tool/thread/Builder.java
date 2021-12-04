package com.vegeta.client.tool.thread;

import java.io.Serializable;

/**
 * Builder pattern interface definition.
 *
 * @Author fuzhiqiang
 * @Date 2021/11/24
 */
public interface Builder<T> extends Serializable {
    T build();
}
