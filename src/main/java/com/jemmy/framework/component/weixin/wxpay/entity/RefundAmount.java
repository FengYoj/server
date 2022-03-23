package com.jemmy.framework.component.weixin.wxpay.entity;

import lombok.Data;

@Data
public class RefundAmount {

    private Integer refund;

    private Integer total;

    private String currency = "CNY";

}
