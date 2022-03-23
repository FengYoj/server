package com.jemmy.framework.component.resources.image;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.component.resources.ResourceController;
import com.jemmy.framework.component.upload.UploadInfo;
import com.jemmy.framework.utils.result.Result;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@AutoAPI("Resource/Image")
public class ImageController extends ResourceController<ResourceImage, ImageRepository> {

    @Override
    public ResourceImage process(UploadInfo info) {
        // 新建资源图片对象
        ResourceImage image = new ResourceImage(info);

        BufferedImage bufferedImage;

        try {
            bufferedImage = ImageIO.read(info.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 写入图片尺寸
        image.setHeight(bufferedImage.getHeight());
        image.setWidth(bufferedImage.getWidth());

        return image;
    }

    public Result<ResourceImage> upload(BufferedImage bi, String name, String folder) throws IOException {

        File file = File.createTempFile("resource_image", ".png");

        ImageIO.write(bi, "png", file);

        return this.upload(file, name, folder);
    }

    public Result<ResourceImage> upload(File file, String name, String folder) throws IOException {

        Result<UploadInfo> result = uploadController.upload(file, name, name, "image", "image/png", folder);

        if (result.isBlank()) {
            return result.toObject();
        }

        UploadInfo uploadInfo = result.getData();

        ResourceImage entity = this.process(uploadInfo);

        uploadInfo.delete();
        file.delete();

        return controller.callbackSave(entity);
    }
}
