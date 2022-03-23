package com.jemmy.framework.component.resources;

import com.jemmy.framework.component.upload.UploadInfo;

import java.io.IOException;

public interface ResourceInterface<T extends Resource> {

    T process(UploadInfo info) throws IOException;

}
