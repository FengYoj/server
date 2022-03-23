package com.jemmy.framework.component.collect;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;

@Data
@Entity
public class Collect extends EntityKey {

    @FieldAttr("标题")
    private String title;

    @TableAttr(disable = true)
    @FieldAttr(value = "内容", type = FieldType.Textarea)
    @Column(columnDefinition = "text")
    private String content;

}
