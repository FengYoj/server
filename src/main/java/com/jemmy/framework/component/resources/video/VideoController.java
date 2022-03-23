package com.jemmy.framework.component.resources.video;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.component.resources.ResourceController;
import com.jemmy.framework.component.resources.ResourceParamConfig;
import com.jemmy.framework.component.resources.image.ImageController;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.component.upload.UploadInfo;
import com.jemmy.framework.utils.file.FileInfo;
import com.jemmy.framework.utils.result.Result;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.io.IOException;

@AutoAPI("Resource/Video")
public class VideoController extends ResourceController<ResourceVideo, VideoRepository> {

    private final ImageController imageController;

    public VideoController(ImageController imageController) {
        this.imageController = imageController;
    }

    @Override
    public ResourceVideo process(UploadInfo info) throws IOException {
        ResourceVideo video = new ResourceVideo(info);

        File file = info.getFile();

        // 获取视频封面
        File cover = VideoUtils.getCover(file);

        ResourceParamConfig config = new ResourceParamConfig();

        String name = info.getName();

        config.setName(name.substring(0, name.lastIndexOf(".")));

        Result<ResourceImage> coverImg = imageController.upload(new FileInfo(cover), config);

        if (coverImg.isBlank()) {
            throw new RuntimeException("Cover image processing exception");
        }

        video.setCover(coverImg.getData());

        MultimediaObject instance = new MultimediaObject(file);

        // 获取视频信息
        MultimediaInfo result;

        try {
            result = instance.getInfo();
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }

        // 写入视频时长
        video.setDuration(result.getDuration());

        // 获取视频尺寸
        VideoSize size = result.getVideo().getSize();

        video.setWidth(size.getWidth());
        video.setHeight(size.getHeight());

        return video;
    }
}
