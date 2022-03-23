package com.jemmy.framework.component.order;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.component.statistics.StatisticsAttr;
import com.jemmy.framework.component.statistics.StatisticsCrucial;
import com.jemmy.framework.component.statistics.StatisticsEntity;
import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.EntityKey;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@StatisticsEntity(crucial = { StatisticsCrucial.class, OrderUserCrucial.class })
@EntityListeners(AuditingEntityListener.class)
public class Order extends EntityKey {

    @FieldAttr(value = "名称", empty = false)
    private String name;

    @FieldAttr(value = "订单号", search = true, empty = false)
    @TableAttr(width = 300)
    private String orderNumber;

    @FieldAttr(value = "支付平台订单号")
    @TableAttr(width = 300)
    private String platformNumber;

    @WebFilter
    @FieldAttr(value = "IP", search = true)
    private String ip;

    @FieldAttr(value = "支付状态", filter = true)
    @SelectField(fixed = { "未支付", "已支付", "已取消", "支付失败" })
    private Integer paymentStatus = 0;

    @FieldAttr(type = FieldType.Price, value = "总金额（单位：元）", empty = false)
    private Integer totalPrice;

    @FieldAttr(type = FieldType.Price, value = "优惠金额（单位：元）")
    private Integer discountPrice = 0;

    @StatisticsAttr(condition = OrderStatisticsCondition.class)
    @FieldAttr(type = FieldType.Price, value = "实际支付金额（单位：元）", empty = false)
    private Integer price;

    @WebFilter
    @ManyToOne
    private User user;

    @TableAttr(disable = true)
    private String sign;

    @TableAttr(disable = true)
    private String refundNumber;

    @TableAttr(disable = true)
    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPlatformNumber() {
        return platformNumber;
    }

    public void setPlatformNumber(String platformNumber) {
        this.platformNumber = platformNumber;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Integer paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Integer discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRefundNumber() {
        return refundNumber;
    }

    public void setRefundNumber(String refundNumber) {
        this.refundNumber = refundNumber;
    }
}
