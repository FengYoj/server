package com.jemmy.framework.controller;

import com.alibaba.fastjson.JSONObject;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

public class ErrorUtils {
    public static Boolean hasErrors(BindingResult bindingResult) {
        return bindingResult != null && bindingResult.hasErrors();
    }

    public static String getErrorStr(BindingResult bindingResult) {
        List<String> list = new ArrayList<>();
        for (ObjectError error : bindingResult.getAllErrors()) {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(error));
            list.add(jsonObject.get("field") + " " + error.getCode());
        }
        return String.join(", ", list);
    }

    public static <T> Result<T> getStatus(BindingResult bindingResult) {
        return new Result<T>(ResultCode.HTTP400).putMessage(getErrorStr(bindingResult));
    }
}
