package com.xbk.core.global.exception;

import com.xbk.core.global.result.FailResponse;

/**
 * <p></p>
 * <p> 异常抽象类
 * <PRE>
 * <BR>    修改记录
 * <BR>-----------------------------------------------
 * <BR>    修改日期         修改人          修改内容
 * </PRE>
 *
 * @author fuzq
 * @date Created in 2021年06月01日 19:55
 * @version 1.0
 * @since 1.0
 */
public abstract class AbstractException extends RuntimeException {

    public AbstractException(String message) {
        super(message);
    }

    /**
     * 异常信息
     */
    protected String message;

    /**
     * 错误返回结果
     */
    protected FailResponse failResponse;

    public FailResponse getFailResponse() {
        return failResponse;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
