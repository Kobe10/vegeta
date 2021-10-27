package com.xbk.core.global.exception.xbk;

public enum WebRespCode {

    SUCCESS(200, "响应成功"),
    NOTFOUND(404, "请求数据不存在"),
    BAD_REQUEST(500, "服务器内部错误"),
    OVER_CLASS_MAX_STUDENT(100001, "班级人数超过上限"),
    CLASS_IN_ERROR(100002, "classIn请求出错"),
    WORKBENCH_NAVIGATION_CONFIGURATION_ERROR(100003, "工作台导航配置错误"),
    FORBIDDEN(403, "权限不足"),
    FAIL(0, "请求失败"),
    NOT_REGISTRY(303, "请联系团队管理员先注册账号！"),
    NOT_LOGIN(300, "用户未登录"),
    LOGIN_EXPIRED(311, "登陆信息已过期，请重新登录"),
    LOGIN_PERMIT_CHANGED(312, "用户信息变更，请重新加载权限"),
    AUTH_FAILED(313, "认证失败");

    private final int code;
    private final String msg;

    WebRespCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }

    public WebRespCode getWebRespCode() {
        return this;
    }
}
