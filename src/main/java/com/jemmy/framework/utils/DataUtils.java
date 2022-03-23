package com.jemmy.framework.utils;

public class DataUtils {

    /**
     * 最小化数据大小
     * @param size 字节
     */
    public static String getMinimizeSize(long size) {
        long rest;

        if (size < 1024){
            return size + "B";
        } else {
            size /= 1024;
        }

        if (size < 1024) {
            return size + "KB";
        } else {
            rest = size % 1024;
            size /= 1024;
        }

        if (size < 1024) {
            size = size * 100;
            return size / 100 + "." + rest * 100 / 1024 % 100 + "MB";
        } else {
            size = size * 100 / 1024;
            return size / 100 + "." + size % 100 + "GB";
        }
    }

}
