package com.jemmy.framework.utils;

import java.util.Arrays;
import java.util.List;

public class CheckUtils {

    @SafeVarargs
    public static <T> Boolean isMatch(T target, T ...attr) {
        List<T> list = Arrays.asList(attr);

        return list.contains(target);
    }

}
