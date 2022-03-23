package com.jemmy.framework.auto.page.entity;

import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.label.LabelConfig;
import com.jemmy.framework.auto.page.type.password.PasswordConfig;
import com.jemmy.framework.auto.page.type.select.SelectConfig;
import com.jemmy.framework.auto.page.type.upload.UploadConfig;
import lombok.Data;

@Data
public class CreateData {

    private String fields = "";

    private String field;

    private String name;

    private String title;

    private Integer length;

    private String placeholder;

    private Boolean required;

    private FieldType type;

    private UploadConfig uploadConfig;

    private SelectConfig selectConfig;

    private EditorConfig editorConfig;

    private LabelConfig labelConfig;

    private PasswordConfig passwordConfig;

    // 序列，值越大越靠前
    private Integer sequence;

    private String where;

    public CreateData(String name, String title, Integer sequence) {
        this.name = name;
        this.title = title;
        this.sequence = sequence;
    }

    public CreateData() {}
}
