package com.jemmy.framework.auto.page.type.upload;

import lombok.Data;

@Data
public class UploadConfig {
    private String type;

    private Boolean multi;

    private String accept;
}
