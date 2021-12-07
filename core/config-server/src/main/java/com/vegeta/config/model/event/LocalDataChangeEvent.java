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
    public final String groupKey;

    public final List<String> betaIps;

    public LocalDataChangeEvent(String groupKey, List<String> betaIps) {
        this.groupKey = groupKey;
        this.betaIps = betaIps;

    }
}
