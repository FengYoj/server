package com.jemmy.framework.service.update;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class Result<T> {

    private String method;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Result() {
    }

    public Result(String method) {
        this.method = method;
    }

    public Result(String method, String message) {
        this.method = method;
        this.message = message;
    }
}
