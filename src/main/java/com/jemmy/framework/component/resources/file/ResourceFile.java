package com.jemmy.framework.component.resources.file;

import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.upload.UploadInfo;

import javax.persistence.Entity;

@Entity
public class ResourceFile extends Resource {

    public ResourceFile() {
    }

    public ResourceFile(UploadInfo info) {
        super(info);
    }
}
