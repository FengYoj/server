package com.jemmy.framework.component.resources;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.upload.UploadConfig;
import com.jemmy.framework.component.upload.UploadController;
import com.jemmy.framework.component.upload.UploadInfo;
import com.jemmy.framework.component.upload.UploadParam;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.controller.JpaRepository;
import com.jemmy.framework.utils.SpringBeanUtils;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.file.FileInfo;
import com.jemmy.framework.utils.file.FileUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ResourceController<E extends Resource, R extends JpaRepository<E>> extends JpaController<E, R> implements ResourceInterface<E> {

    protected static final UploadController uploadController = SpringBeanUtils.getBean(UploadController.class);

    @Post
    public JemmyJson ckeditor(@AutoParam MultipartFile upload) throws IOException {
        // 上传文件
        Result<UploadInfo> result = uploadController.upload(upload);

        JemmyJson res = new JemmyJson();

        if (result.isBlank()) {
            res.put("uploaded", false);
            return res;
        }

        res.put("uploaded", true);
        res.put("url", result.getData().getUrl());

        return res;
    }

    @Post(path = { RequestPath.WEB, RequestPath.ADMIN })
    public Result<List<E>> upload(@AutoParam MultipartFile[] file, @AutoParam(required = false) String type, @AutoParam(required = false) String name, @AutoParam(required = false) String folder) throws IOException {
        if (file == null || file.length < 1) {
            return Result.<List<E>>of(ResultCode.HTTP400).putMessage("File cannot be empty");
        }

        UploadConfig config = new UploadConfig(name, folder);

        List<E> res = new ArrayList<>();

        UploadParam param = new UploadParam();

        param.setType(type);

        for (MultipartFile item : file) {

            // 上传文件
            Result<UploadInfo> result = uploadController.upload(item, param, config);

            if (result.isBlank()) {
                return result.toObject();
            }

            UploadInfo info = result.getData();

            E entity = this.process(info);

            res.add(controller.callbackSave(entity).get());

            // Delete file
            info.delete();
        }

        return Result.<List<E>>of(ResultCode.HTTP200).setData(res);
    }

    public Result<E> upload(FileInfo info, ResourceParamConfig config) throws IOException {
        if (info == null || info.getFile().length() <= 0) {
            return Result.<E>of(ResultCode.HTTP400).putMessage("File cannot be empty");
        }

        UploadParam param = new UploadParam();

        param.setName(config.getName());
        param.setFolder(config.getFolder());

        // 上传文件
        Result<UploadInfo> result = uploadController.upload(info, param);

        if (result.isBlank()) {
            return result.toObject();
        }

        UploadInfo uploadInfo = result.getData();

        E entity = this.process(uploadInfo);

        // 素材库是否可见
        entity.setMaterial(config.getMaterial());

        uploadInfo.delete();

        return controller.callbackSave(entity);
    }

    /**
     * 上传 网络文件
     * @param url 路径
     */
    public Result<E> upload(String url) throws IOException {
        return this.upload(url, new ResourceParamConfig());
    }

    /**
     * 上传 网络文件
     * @param url 路径
     * @param config 配置参数
     */
    public Result<E> upload(String url, ResourceParamConfig config) throws IOException {

        if (StringUtils.isBlank(config.getName())) {
            // url 获取文件名
            String name = url.substring(url.lastIndexOf("/") + 1);

            // 写入名称
            config.setName(name);
        }

        // 下载文件
        FileInfo info = FileUtils.download(url);

        return this.upload(info, config);
    }

    /**
     * 上传 本地文件
     * @param file 文件
     * @param config 配置参数
     */
    public Result<E> upload(File file, ResourceParamConfig config) throws IOException {

        if (StringUtils.isBlank(config.getName())) {
            // url 获取文件名
            String name = file.getName();

            // 写入名称
            config.setName(name);
        }

        // 下载文件
        FileInfo info = new FileInfo(file);

        return this.upload(info, config);
    }

//    public Status<E> parsing(String url) throws IOException {
//        // 下载文件
//        FileInfo info = FileUtils.download(url);
//    }
}
