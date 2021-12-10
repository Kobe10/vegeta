package com.vegeta.config.service;

import com.vegeta.config.model.event.LocalDataChangeEvent;
import com.vegeta.global.notify.NotifyCenter;

/**
 * ConfigChangePublisher.
 *
 * @author fuzhiqiang
 */
public class ConfigChangePublisher {

    /**
     * Notify ConfigChange.
     *
     * @param event ConfigDataChangeEvent instance.
     */
    public static void notifyConfigChange(LocalDataChangeEvent event) {
        NotifyCenter.publishEvent(event);
    }
}
