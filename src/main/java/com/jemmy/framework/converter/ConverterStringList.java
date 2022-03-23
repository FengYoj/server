package com.jemmy.framework.converter;

import com.jemmy.framework.component.json.JemmyArray;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

@Converter
public class ConverterStringList implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> meta) {
        return JemmyArray.toJSONString(meta);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        return JemmyArray.toJavaList(dbData);
    }
}
