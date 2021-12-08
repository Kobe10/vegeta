package com.vegeta.client.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Config empty exception.
 *
 * @author fuzhiqiang
 * @date 2021/11/28 21:58
 */
@Data
@AllArgsConstructor
public class ConfigEmptyException extends RuntimeException {

    private String description;

    private String action;
}
