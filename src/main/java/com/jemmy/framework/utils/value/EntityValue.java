package com.jemmy.framework.utils.value;

public class EntityValue<T> {

    private final T content;

    public EntityValue(T content) {
        this.content = content;
    }

    public T getContent() {
        return content;
    }
}
