package com.jemmy.config;

import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;

@AutoConfig(value = "阿里云 OSS 配置", menu = MenuType.SETTING)
public class AliOssConfig {

    public static String endpoint = "";

    public static String accessKeyId = "";

    public static String accessKeySecret = "";

    public static String bucketName = "";

    public static String bucketDomain = "";

}
