package com.jemmy.framework.utils;

public class IntegerUtils {

    public static Boolean equals(Integer target, Integer ...integers) {
        for (Integer integer : integers) {
            if (target.equals(integer)) {
                return true;
            }
        }

        return false;
    }
}
