package com.jemmy.framework.exception;

import com.jemmy.framework.component.message.Message;

public class ExceptionMessage extends RuntimeException {

    public ExceptionMessage(String message, String additional) {
        super(message);
        Message.error(message, additional);
    }

    public ExceptionMessage(String message) {
        super(message);
        Message.error(message);
    }

}
