package com.jemmy.framework.component.resources.video;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.component.upload.UploadInfo;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class ResourceVideo extends Resource {

    @FieldAttr("宽度")
    private Integer width;

    @FieldAttr("高度")
    private Integer height;

    @FieldAttr("时长")
    private Long duration;

    @OneToOne
    @FieldAttr("封面")
    @CreateAttr(disable = true)
    private ResourceImage cover;

    public ResourceVideo() {
    }

    public ResourceVideo(UploadInfo info) {
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

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public ResourceImage getCover() {
        return cover;
    }

    public void setCover(ResourceImage cover) {
        this.cover = cover;
    }
}
