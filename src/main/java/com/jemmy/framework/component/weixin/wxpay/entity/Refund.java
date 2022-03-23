package com.jemmy.framework.component.weixin.wxpay.entity;

import lombok.Data;

@Data
public class Refund {
    private String out_trade_no;

    private String out_refund_no;

    private String reason;

    private String notify_url;

    private RefundAmount amount;
}
