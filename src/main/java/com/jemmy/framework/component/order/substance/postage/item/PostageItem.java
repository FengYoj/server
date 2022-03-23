package com.jemmy.framework.component.order.substance.postage.item;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.converter.ConverterStringList;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Convert;
import javax.persistence.Entity;
import java.util.List;

@Entity
public class PostageItem extends EntityKey {

    @FieldAttr(value = "地区", empty = false)
    @SelectField(fixed = {"河北", "山西", "吉林", "辽宁", "黑龙江", "陕西", "甘肃", "青海", "山东", "福建", "浙江",
            "河南", "湖北", "湖南", "江西", "江苏", "安徽", "广东", "海南", "四川", "贵州", "云南", "北京", "上海", "天津",
            "重庆", "内蒙古", "新疆", "宁夏", "广西", "西藏"})
    @Convert(converter = ConverterStringList.class)
    private List<String> area;

    @FieldAttr(type = FieldType.Price, value = "首重价格（1Kg）", empty = false)
    private Integer price;

    @FieldAttr(type = FieldType.Price, value = "续重价格（1Kg）", empty = false)
    private Integer increasePrice;

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getIncreasePrice() {
        return increasePrice;
    }

    public void setIncreasePrice(Integer increasePrice) {
        this.increasePrice = increasePrice;
    }
}
