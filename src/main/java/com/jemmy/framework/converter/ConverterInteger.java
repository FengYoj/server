package com.jemmy.framework.converter;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Converter
public class ConverterInteger implements AttributeConverter<List<Integer>, String> {
    @Override
    public String convertToDatabaseColumn(List meta) {

        if (meta == null) {
            return null;
        }

        return StringUtils.join(meta, ",");
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {

        if (dbData == null) {
            return null;
        }

        List<Integer> list = new ArrayList<>();
        CollectionUtils.collect(Arrays.asList(dbData.split(",")), o -> Integer.valueOf(o.toString()), list);
        return list;
    }
}
