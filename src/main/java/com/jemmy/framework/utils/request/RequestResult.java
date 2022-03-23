package com.jemmy.framework.utils.request;

import com.jemmy.framework.component.json.JemmyArray;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;

public class RequestResult {

    // 状态码
    private final int status;

    // 信息
    private String message;

    private String content;

    public RequestResult(int status) {
        this.status = status;
    }

    public RequestResult(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public RequestResult(int status, String message, String content) {
        this.status = status;
        this.message = message;
        this.content = content;
    }

    public JemmyJson toJson() {
        return new JemmyJson(content);
    }

    public JemmyArray toArray() {
        return new JemmyArray(content);
    }

    public Result<JemmyJson> toJsonStatus() {
        if (status == 200) {
            return Result.<JemmyJson>HTTP200().setData(toJson());
        }

        return Result.of(ResultCode.of(status, message));
    }

    public Result<JemmyArray> toArrayStatus() {
        if (status == 200) {
            return Result.<JemmyArray>HTTP200().setData(toArray());
        }

        return Result.of(ResultCode.of(status, message));
    }

    public <T> Result<T> toStatus() {
        if (status == 200) {
            return Result.<T>HTTP200().setData(JemmyJson.toJavaObject(content));
        }

        return Result.of(ResultCode.of(status, message));
    }

    public Boolean isNormal() {
        return status == 200;
    }

    public Boolean isBlank() {
        return status != 200;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return content;
    }
}
