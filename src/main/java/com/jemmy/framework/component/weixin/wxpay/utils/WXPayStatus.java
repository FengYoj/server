package com.jemmy.framework.component.weixin.wxpay.utils;

import lombok.Data;

@Data
public class WXPayStatus {

    private String code;

    private String message;

    public WXPayStatus(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static WXPayStatus Success() {
        return new WXPayStatus("SUCCESS", "成功");
    }

}
