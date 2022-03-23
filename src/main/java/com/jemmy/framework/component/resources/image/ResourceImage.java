package com.jemmy.framework.component.resources.image;

import com.jemmy.config.PathConfig;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.upload.UploadInfo;
import com.jemmy.framework.utils.ImageUtils;

import javax.imageio.ImageIO;
import javax.persistence.Entity;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

@Entity
public class ResourceImage extends Resource {

    @FieldAttr("宽度")
    private Integer width;

    @FieldAttr("高度")
    private Integer height;

    public ResourceImage() {
    }

    public ResourceImage(UploadInfo info) {
        super(info);
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    @JsonIgnore
    public BufferedImage getBufferedImage() throws IOException, InterruptedException {
        if (this.getOss()) {
            return ImageUtils.getImage(this.getUrl());
        } else {
            return ImageIO.read(new File(PathConfig.upload + this.getSrc()));
        }
    }

    @JsonIgnore
    public Image getImage() throws IOException, InterruptedException {
        return new ImageIcon(getBufferedImage()).getImage();
    }
}
