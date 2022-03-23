package com.jemmy.framework.component.ali.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.jemmy.config.AliOssConfig;

import java.io.File;
import java.io.InputStream;

public class AliOssController {

    public static void uploadFile(InputStream inputStream, String name) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(AliOssConfig.endpoint, AliOssConfig.accessKeyId, AliOssConfig.accessKeySecret);

        // 上传文件流
        ossClient.putObject(AliOssConfig.bucketName, name, inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
    }

    public static void uploadFile(File file, String name) {
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(AliOssConfig.endpoint, AliOssConfig.accessKeyId, AliOssConfig.accessKeySecret);

        // 上传文件流
        ossClient.putObject(AliOssConfig.bucketName, name, file);

        // 关闭OSSClient。
        ossClient.shutdown();
    }
}
