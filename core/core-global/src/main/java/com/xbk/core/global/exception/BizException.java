package com.xbk.core.global.exception;

import com.xbk.core.global.exception.common.BizExceptionInterface;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Objects;

/**
 * <p></p>
 * <p> 业务异常
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
@Data
@EqualsAndHashCode(callSuper = true)
public class BizException extends AbstractException {

    /**
     * 默认业务异常状态码
     */
    private static final Integer DEFAULT_CODE = 4001001;

    public BizException(String message) {
        super(message);
        this.code = DEFAULT_CODE;
        this.message = message;
    }

    @Builder
    public BizException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 通过公共的异常信息
     *
     * @param exceptionInterface
     * @return
     */
    public static BizException of(BizExceptionInterface exceptionInterface, Object... messageInfo) {
        BizException bizException = BizException.builder().code(exceptionInterface.getCode()).build();
        String message = exceptionInterface.getMessage();
        if (Objects.nonNull(messageInfo) && messageInfo.length > 0) {
            message = String.format(message, messageInfo);
        }
        bizException.setMessage(message);
        return bizException;
    }

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String message;

}
