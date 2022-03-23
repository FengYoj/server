package com.jemmy.framework.exception;

import com.jemmy.framework.utils.result.Result;

public class ResultException extends RuntimeException {

    private Result<?> result;

    public ResultException(Result<?> result) {
        this.result = result;
    }

    public ResultException(String msg) {
        super(msg);
    }

    public Result<?> getStatus() {
        return result;
    }
}
