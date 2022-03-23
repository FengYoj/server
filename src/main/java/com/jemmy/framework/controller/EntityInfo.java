package com.jemmy.framework.controller;

import com.jemmy.framework.auto.page.type.FieldType;
import lombok.Data;

@Data
public class EntityInfo {

    private String name;

    private String title;

    private FieldType type;

}
