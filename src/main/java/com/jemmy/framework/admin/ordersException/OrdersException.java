package com.jemmy.framework.admin.ordersException;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.controller.EntityKey;
import lombok.Data;

import javax.persistence.Entity;

@Data
@Entity
public class OrdersException extends EntityKey {

    @FieldAttr("订单号")
    private String orderId;

    @FieldAttr("错误码")
    private String code;

    @FieldAttr(value = "支付状态", filter = true)
    @SelectField(fixed = { "0:未知", "1:已支付", "2:未支付" })
    private Integer paymentStatus = 0;

    @FieldAttr(value = "支付平台")
    @SelectField(fixed = { "wxPay:微信支付" })
    private String paymentPlatform;

    @FieldAttr("错误信息")
    private String message;

}
