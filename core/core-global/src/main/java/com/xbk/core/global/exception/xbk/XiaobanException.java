package com.xbk.core.global.exception.xbk;

/**
 * 抛出业务异常直接返回给调用方
 */
public class XiaobanException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String msg;

    private int code = WebRespCode.FAIL.getCode();

    private String data;

    /**
     * 抛出指定提示的异常消息
     */
    public XiaobanException(String msg) {
        super(msg);
        this.msg = msg;
    }

    /**
     * 根据状态码构建错误信息，可以省去重复书写相同的message
     */
    public XiaobanException(WebRespCode code) {
        super(code.getMsg());
        this.msg = code.getMsg();
        this.code = code.getCode();
    }

    public XiaobanException(WebRespCode code, String data) {
        super(code.getMsg());
        this.msg = code.getMsg();
        this.code = code.getCode();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return this.msg;
    }

    public String getData() {
        return this.data;
    }

}