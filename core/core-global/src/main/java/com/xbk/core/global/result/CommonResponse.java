package com.xbk.core.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

/**
 * <p></p>
 * <p> 基础返回结果对象
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
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {

    /**
     * 状态码
     */
    protected Integer code;

    /**
     * 状态值
     */
    protected String status;

    /**
     * 描述信息
     */
    protected String message;

    /**
     * 返回数据体
     */
    protected T data;

    @Override
    public String toString() {
        return String.format("{ \"code\": %s ,\"status\": \"%s\",\"message\": \"%s\", \"data\": \"%s\"}",
                code, status, message, Objects.isNull(data) ? "null" : data.toString());
    }
}
