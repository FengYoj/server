package com.jemmy.framework.service.feedback;

import lombok.Data;

@Data
public class Feedback<T> {
    private String type;

    private String message;

    private T data;
}
