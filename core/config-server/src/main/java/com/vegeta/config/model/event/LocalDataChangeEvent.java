package com.vegeta.config.model.event;

import com.vegeta.global.notify.Event;

import java.util.List;

/**
 * LocalDataChangeEvent.
 *
 * @author fuzhiqiang
 */
public class LocalDataChangeEvent extends Event {

    private static final long serialVersionUID = -127792427115335383L;
    /**
     * 租户+项目+线程池
     */
    public final String groupKey;

    /**
     * 客户端实例唯一标识
     */
    public final String identify;

    public LocalDataChangeEvent(String groupKey, String identify) {
        this.groupKey = groupKey;
        this.identify = identify;
    }
}
