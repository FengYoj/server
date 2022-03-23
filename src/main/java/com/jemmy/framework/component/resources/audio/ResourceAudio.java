package com.jemmy.framework.component.resources.audio;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.upload.UploadInfo;

import javax.persistence.Entity;

@Entity
public class ResourceAudio extends Resource {

    @FieldAttr("时长")
    private Long duration;

    public ResourceAudio() {
    }

    public ResourceAudio(UploadInfo info) {
        super(info);
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
