package com.vegeta.logrecord.model;

import lombok.*;

import javax.validation.constraints.NotNull;

/**
 * 方法执行结果.
 *
 * @Author fuzhiqiang
 * @Date 2021/12/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class MethodExecuteResult {

    /**
     * 是否成功
     */
    @NotNull
    private boolean success;

    /**
     * 异常
     */
    private Throwable throwable;

    /**
     * 错误日志
     */
    private String errorMsg;

    public MethodExecuteResult(boolean b) {
        this.success = b;
    }
}
