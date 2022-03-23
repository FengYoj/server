package com.jemmy.framework.converter;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.framework.component.location.Address;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ConverterAddress implements AttributeConverter<Address, String> {
    @Override
    public String convertToDatabaseColumn(Address meta) {
        return JSONObject.toJSONString(meta);
    }

    @Override
    public Address convertToEntityAttribute(String dbData) {
        return JSONObject.parseObject(dbData, Address.class);
    }
}