package com.jemmy.framework.utils.result;

public interface ResultCallback<T> {

    default void success(T data) {}

    default void fail(ResultCode code, String message){}

}
