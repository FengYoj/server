package com.jemmy.framework.utils.file;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.util.stream.Collectors;

public class ClassPathResourceReader {

    // 文件路径
    private final String path;

    // 文件内容
    private String content;

    public ClassPathResourceReader(String path) {
        this.path = path;
    }

    public String getContent() {
        if (content == null) {
            try {
                ClassPathResource resource = new ClassPathResource(path);
                BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
                content = reader.lines().collect(Collectors.joining("\n"));
                reader.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return content;
    }

    public static File getFile(String path) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(path);

        File targetFile = File.createTempFile("avatar", ".png");
        FileUtils.copyFile(classPathResource.getFile(), targetFile);

        return targetFile;
    }

    public static ClassPathResourceReader of(String path) {
        return new ClassPathResourceReader(path);
    }
}
