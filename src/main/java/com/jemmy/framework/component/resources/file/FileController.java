package com.jemmy.framework.component.resources.file;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.component.resources.ResourceController;
import com.jemmy.framework.component.upload.UploadInfo;

@AutoAPI("Resource/File")
public class FileController extends ResourceController<ResourceFile, FileRepository> {

    @Override
    public ResourceFile process(UploadInfo info) {
        return new ResourceFile(info);
    }

}
