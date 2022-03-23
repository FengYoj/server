package com.jemmy.framework.utils.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.utils.StringUtils;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

public class Result<T> {
    // 状态码
    private final int status;

    // 信息
    private String message;

    // 数据
    private T data = null;

    // code 用于判断错误类型
    private Object code;

    // 附加错误信息
    private String info;

    private final List<String> messages = new ArrayList<>();

    private final ResultCode resultCode;

    public Result(ResultCode status) {
        this.resultCode = status;
        this.status = status.getCode();
        this.message = status.getMessage();
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public Result<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> setInfo(String info) {
        this.info = info;
        return this;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result<T> putMessage(String message) {
        if (!StringUtils.isBlank(message)) {
            messages.add(message);
        }
        return this;
    }

    @Transient
    public Boolean isNormal() {
        return status == 200 || status == 201;
    }

    @Transient
    public Boolean isBlank() {
        return !isNormal();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T getData() {
        return data;
    }

    public Result<T> toEmpty() {
        this.data = null;
        return this;
    }

    public String getMessage() {
        if (messages.size() > 0) {
            message += ", 信息: " + String.join(", ", messages);
        }

        return message;
    }

    public int getStatus() {
        return status;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Object getCode() {
        return code;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getInfo() {
        return info;
    }

    /**
     * 如果遇到异常时抛出：ResultException
     */
    public Result<T> throwout() {
        if (isBlank()) {
            throw new ResultException(this);
        }

        return this;
    }

    /**
     * 直接获取数据，状态异常时抛出：ResultException
     */
    @Transient
    public T get() {
        // 如果遇到异常将抛出
        throwout();
        // 返回数据
        return data;
    }

    /**
     * 监听回调函数
     * @param callback 回调接口
     */
    public void on(ResultCallback<T> callback) {
        if (isBlank()) {
            callback.fail(resultCode, message);
        } else {
            callback.success(data);
        }
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                ", info='" + info + '\'' +
                ", messages=" + messages +
                ", statusCode=" + resultCode +
                '}';
    }

    public static <O> Result<O> HTTP200() {
        return new Result<>(ResultCode.HTTP200);
    }

    public static <O> Result<O> HTTP200(O o) {
        return new Result<O>(ResultCode.HTTP200).setData(o);
    }

    public static <O> Result<O> HTTP404() {
        return new Result<>(ResultCode.HTTP404);
    }

    public static <O> Result<O> HTTP403() {
        return new Result<>(ResultCode.HTTP403);
    }

    public static <O> Result<O> HTTP400() {
        return new Result<>(ResultCode.HTTP400);
    }

    public static <O> Result<O> HTTP400(String msg) {
        return new Result<O>(ResultCode.HTTP400).putMessage(msg);
    }

    public static <O> Result<O> HTTP400(String msg, String code) {
        return new Result<O>(ResultCode.HTTP400).putMessage(msg).setCode(code);
    }

    public static <O> Result<O> HTTP500() {
        return new Result<>(ResultCode.HTTP500);
    }

    public static <O> Result<O> HTTP500(String msg) {
        return new Result<O>(ResultCode.HTTP500).setMessage(msg);
    }

    public <O> Result<O> toObject() {
        return new Result<O>(resultCode).setMessage(this.getMessage());
    }

    public static <O> Result<O> of(ResultCode status) {
        return new Result<>(status);
    }

    public static <O> Result<O> data(O data) {
        if (data == null) {
            return Result.HTTP404();
        }
        return Result.HTTP200(data);
    }

    public static <O> Result<O> code(String code) {
        return Result.<O>HTTP400().setCode(code);
    }

    public static <O> Result<O> code(String code, String message) {
        return Result.<O>HTTP400().setCode(code).setMessage(message);
    }
}
