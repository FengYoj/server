package com.jemmy.framework.utils.file;

import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;

import java.io.File;
import java.io.FileOutputStream;

public class ByteFileUtils {

    private final byte[] bytes;

    public ByteFileUtils(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
     * Save multipart file
     * @param path save path
     * @param name file name
     * @return file name
     */
    public Result<String> save(String path, String name) {
        File targetFile = new File(path);

        // Does not exist and fails to create
        if (!targetFile.exists() && !targetFile.mkdirs()) {
            return Result.of(ResultCode.HTTP400);
        }

        try {
            FileOutputStream out = new FileOutputStream(path + name);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (Exception e) {
            return Result.<String>of(ResultCode.HTTP500).putMessage(e.getMessage());
        }

        return Result.<String>of(ResultCode.HTTP200).setData(name);
    }

    /**
     * Use of instead of new character
     * @param bytes byte list
     * @return ByteFileUtils Constructor
     */
    public static ByteFileUtils of(byte[] bytes) {
        return new ByteFileUtils(bytes);
    }
}
