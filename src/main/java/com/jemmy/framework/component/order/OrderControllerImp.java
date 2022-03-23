package com.jemmy.framework.component.order;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.exception.ResultException;
import com.jemmy.framework.utils.result.Result;

public interface OrderControllerImp<E> {

    default void after(E order) {}

    default void before(OrderParam param, E order) {
        throw new ResultException(Result.HTTP500("The before function is not configured"));
    }

}
