package com.jemmy.framework.component.upload;

import com.jemmy.framework.component.ali.oss.AliOssController;
import com.jemmy.config.PathConfig;
import com.jemmy.framework.utils.file.ByteFileUtils;
import com.jemmy.framework.utils.file.FileInfo;
import com.jemmy.framework.utils.file.FileUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@RestController
public class UploadController {

    private final static MimetypesFileTypeMap typeUtils = new MimetypesFileTypeMap();

//    @PostAdmin
//    @AuthCheckerToken(status = true)
//    public Status<List<String>> files(@RequestBody MultipartFile[] files, @RequestParam(required = false) String type) throws IOException {
//        var list = new ArrayList<String>();
//
//        for (var file : files) {
//            var status = file(file, type);
//
//            if (status.getStatus() == 200) {
//                list.add(status.getData());
//            } else {
//                return status.toObject();
//            }
//        }
//
//        return new Status<List<String>>(StatusCode.HTTP200).setData(list);
//    }

    public Result<UploadInfo> upload(MultipartFile file) throws IOException {
        return this.upload(file, new UploadParam(), new UploadConfig());
    }

    public Result<UploadInfo> upload(MultipartFile file, UploadParam param, UploadConfig config) throws IOException {

        String contentType = file.getContentType();

        // File type is blank
        if (StringUtils.isBlank(contentType)) {
            return Result.<UploadInfo>of(ResultCode.HTTP400).putMessage("Unknown file type");
        }

        String[] types = contentType.split("/");

        // 可选指定上传文件的类型
        String type = param.getType();

        // Determine file type support
        if (StringUtils.isExist(type) && !type.equals(types[0])) {
            return Result.<UploadInfo>of(ResultCode.HTTP400).setMessage("File type only supports " + type);
        }

        String fileName;

        if (StringUtils.isExist(config.getName())) {
            fileName = config.getName() + "." + types[1];
        } else {
            // Use UUID as the file name
            fileName = UUID.randomUUID().toString() + "." + types[1];
        }

        File f = File.createTempFile(UUID.randomUUID().toString(), "." + types[1]);

//        if (!f.exists() && !f.createNewFile()) {
//            throw new RuntimeException("Failed to create folder");
//        }

        // MultipartFile to File
        FileUtils.inputStreamToFile(file.getInputStream(), f);

        return this.upload(f, fileName, file.getOriginalFilename(), types[0], contentType, StringUtils.defaults(config.getFolder(), param.getFolder()));
    }

    public Result<UploadInfo> upload(FileInfo info, UploadParam param) throws IOException {
        // 获取内容类型
        String contentType = info.getContentType();

        // 分隔内容类型属性
        String[] types = contentType.split("/");

        File file = info.getFile();

        return upload(file, file.getName(), param.getName(), types[0], contentType, param.getFolder());
    }

    public Result<UploadInfo> upload(File file, String name, String originalName, String type, String contentType, String folder) throws IOException {

        // 上传路径
        String path;
        // 上传文件类型
        UploadType uploadType;
        // 文件夹，根据文件类型，非 image / video / audio 即为 file
        String f = type;

        switch (type) {
            case "image":
                path = PathConfig.uploadImage;
                uploadType = UploadType.IMAGE;
                break;
            case "video":
                path = PathConfig.uploadVideo;
                uploadType = UploadType.VIDEO;
                break;
            case "audio":
                path = PathConfig.uploadAudio;
                uploadType = UploadType.AUDIO;
                break;
            default:
                path = PathConfig.uploadFile;
                uploadType = UploadType.FILE;

                // 其余文件归为 file 文件夹下
                f = "file";
        }

        if (StringUtils.isBlank(folder)) {
            folder = "/";
        } else {
            folder = "/" + folder + "/";
        }

        // Upload resource info
        UploadInfo info = new UploadInfo(file, uploadType, file.length(), StringUtils.isBlank(originalName) ? file.getName() : originalName, contentType);

        if (PathConfig.uploadToOss) {
            String src = f + folder + name;

            // Upload to Ali OSS
            AliOssController.uploadFile(file, src);

            info.setSrc(src);
            // Upload to oss
            info.setOss(true);
        } else {
            if (!FileUtils.of(file).save(path + folder + name)) {
                return Result.<UploadInfo>of(ResultCode.HTTP400).putMessage("File save failed");
            }

            info.setSrc("/" + f + folder + name);
        }

        return Result.<UploadInfo>of(ResultCode.HTTP200).setData(info);
    }

    public Result<String> imageByPath(@RequestParam String path) {
        URL url;
        BufferedInputStream in;
        try {
            String fileName = UUID.randomUUID().toString() + "." + StringUtils.defaults(path.split("\\.")[path.split("\\.").length - 1], "png");
            url = new URL(path);
            in = new BufferedInputStream(url.openStream());

            // 文件存放路径
            String filePath = PathConfig.uploadImage;

            Result<String> save = ByteFileUtils.of(in.readAllBytes()).save(filePath, fileName);

            if (save.isBlank()) {
                return save;
            }

            // 返回图片的存放路径
            return new Result<String>(ResultCode.HTTP200).setData("/image/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<String>(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

    public Result<String> videoByPath(@RequestParam String path, @RequestParam(required = false) String name) {
        URL url;
        BufferedInputStream in;
        try {
            String fileName;

            if (StringUtils.isBlank(name)) {
                fileName = UUID.randomUUID().toString() + "." + StringUtils.defaults(path.split("\\.")[path.split("\\.").length - 1], "mp4");
            } else {
                fileName = name;
            }

            url = new URL(path);
            in = new BufferedInputStream(url.openStream());

            Result<String> save = ByteFileUtils.of(in.readAllBytes()).save(PathConfig.uploadVideo, fileName);

            if (save.isBlank()) {
                return save;
            }

            // 返回图片的存放路径
            return new Result<String>(ResultCode.HTTP200).setData("/video/" + fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<String>(ResultCode.HTTP500).putMessage(e.getMessage());
        }
    }

//    @PostAdmin
//    public Status<Video> video(@RequestBody MultipartFile multipartFile) throws IOException, EncoderException {
//        Status<String> upload = this.upload(multipartFile, "video");
//
//        File file = FileUtils.multipartFileToFile(multipartFile);
//
//        Video video = new Video();
//
//        MultimediaObject instance = new MultimediaObject(file);
//        MultimediaInfo result = instance.getInfo();
//
//        VideoSize size = result.getVideo().getSize();
//
//        video.setDuration(result.getDuration() / 1000);
//        video.setWidth(size.getWidth());
//        video.setHeight(size.getHeight());
//        video.setSrc(upload.getData());
//
//        Status<String> cover = this.file()
//
//
//        return Status.<Video>of(StatusCode.HTTP200).setData(video);
//    }
}
