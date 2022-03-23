package com.jemmy.framework.utils.result;

public class ResultCode {
    public static ResultCode HTTP200 = new ResultCode(200, "Success");
    public static ResultCode HTTP201 = new ResultCode(201, "超级权限，返回对应数据");
    public static ResultCode HTTP203 = new ResultCode(203, "提示");
    public static ResultCode HTTP400 = new ResultCode(400, "参数异常");
    public static ResultCode HTTP403 = new ResultCode(403, "请求权限异常");
    public static ResultCode HTTP404 = new ResultCode(404, "资源不存在");
    public static ResultCode HTTP409 = new ResultCode(409, "资源冲突");
    public static ResultCode HTTP500 = new ResultCode(500, "服务器异常");
    public static ResultCode HTTP504 = new ResultCode(504, "连接超时");

    private final Integer code;
    private final String message;

    public ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public <T> Result<T> getInstance() {
        return Result.of(this);
    }

    public static ResultCode of(Integer code, String message) {
        return new ResultCode(code, message);
    }
}
