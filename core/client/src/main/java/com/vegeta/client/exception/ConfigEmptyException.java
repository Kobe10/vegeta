package com.vegeta.client.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Config empty exception.
 *
 * @author fuzhiqiang
 * @date 2021/11/28 21:58
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ConfigEmptyException extends RuntimeException {

    private static final long serialVersionUID = -6296648414633678725L;

    private String description;

    private String action;
}
