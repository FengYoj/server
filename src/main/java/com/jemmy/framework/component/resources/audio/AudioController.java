package com.jemmy.framework.component.resources.audio;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.component.resources.ResourceController;
import com.jemmy.framework.component.upload.UploadInfo;
import ws.schild.jave.EncoderException;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

@AutoAPI("Resource/Audio")
public class AudioController extends ResourceController<ResourceAudio, AudioRepository> {

    @Override
    public ResourceAudio process(UploadInfo info) {

        ResourceAudio audio = new ResourceAudio(info);

        MultimediaObject instance = new MultimediaObject(info.getFile());

        // 获取音频信息
        MultimediaInfo result;

        try {
            result = instance.getInfo();
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }

        // 写入音频时长
        audio.setDuration(result.getDuration());

        return audio;
    }
}
