package com.jemmy.framework.component.location.area;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;

@Entity
public class Area extends EntityKey {

    @FieldAttr("省")
    private String province;

    @FieldAttr("市")
    private String city;

    @FieldAttr("区")
    private String districts;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistricts() {
        return districts;
    }

    public void setDistricts(String districts) {
        this.districts = districts;
    }
}
