package com.jemmy.framework.converter;

import com.jemmy.framework.component.json.JemmyArray;
import com.jemmy.framework.utils.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class ConverterList implements AttributeConverter<List<Object>, String> {
    @Override
    public String convertToDatabaseColumn(List<Object> meta) {
        return JemmyArray.toJSONString(meta);
    }

    @Override
    public List<Object> convertToEntityAttribute(String dbData) {

        if (StringUtils.isBlank(dbData)) {
            return new ArrayList<>();
        }

        return JemmyArray.toJavaList(dbData);
    }
}
