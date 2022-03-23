/*
 * © Copyright. LeapAd Network - All Rights Reserved
 * 准动网络科技-银豹收银系统
 */
package com.jemmy.framework.utils;

import org.springframework.util.DigestUtils;

import java.util.UUID;

public class MD5Utils {

    public static String encrypt(String sourceStr) {
        return DigestUtils.md5DigestAsHex(sourceStr.getBytes());
    }

    /**
     * 获取 MD5 加密后的 UUID
     *
     * @return MD5 UUID
     */
    public static String getMD5UUID() {
        return encrypt(UUID.randomUUID().toString());
    }
}
