package com.jemmy.framework.utils;

public class NumberUtils {

    /**
     * 当整形为 null 是赋值默认值
     * @param integer 整形
     * @param defaults 默认值
     * @return 整形
     */
    public static Integer defaults(Integer integer, Integer defaults) {
        return integer == null ? defaults : integer;
    }
}
