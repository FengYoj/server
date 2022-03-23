package com.jemmy.framework.converter;

import com.jemmy.framework.component.json.JemmyJson;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ConverterJson implements AttributeConverter<Object, String> {

    @Override
    public String convertToDatabaseColumn(Object meta) {
        return JemmyJson.toJSONString(meta);
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        return new JemmyJson(dbData).toJavaObject();
    }

}