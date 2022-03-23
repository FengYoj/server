package com.jemmy.framework.component.order.substance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.component.order.Order;
import com.jemmy.framework.component.order.OrderUserCrucial;
import com.jemmy.framework.component.order.substance.address.PackageAddress;
import com.jemmy.framework.component.order.substance.message.OrderMessage;
import com.jemmy.framework.component.order.substance.postage.Postage;
import com.jemmy.framework.component.statistics.StatisticsCrucial;
import com.jemmy.framework.component.statistics.StatisticsEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

/**
 * 实物订单
 */
@MappedSuperclass
@StatisticsEntity(crucial = { StatisticsCrucial.class, OrderUserCrucial.class })
@EntityListeners(AuditingEntityListener.class)
public class SubstanceOrder extends Order {

    @JsonIgnore
    @FieldAttr("运费实体")
    @OneToOne
    private Postage postage;

    @FieldAttr(type = FieldType.Price, value = "运费")
    private Integer postagePrice;

    @OneToOne
    private PackageAddress address;

    @OneToMany
    private List<OrderMessage> messages;

    public Postage getPostage() {
        return postage;
    }

    public void setPostage(Postage postage) {
        this.postage = postage;
    }

    public Integer getPostagePrice() {
        return postagePrice;
    }

    public void setPostagePrice(Integer postagePrice) {
        this.postagePrice = postagePrice;
    }

    public PackageAddress getAddress() {
        return address;
    }

    public void setAddress(PackageAddress address) {
        this.address = address;
    }

    public List<OrderMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<OrderMessage> messages) {
        this.messages = messages;
    }
}
