package com.jemmy.framework.utils;

import java.util.UUID;

public class UuidUtils {

    /**
     * 生成uuid32位
     * @return
     */
    public static String getUUID32(){
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

}
