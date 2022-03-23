package com.jemmy.framework.component.order.substance.address;

import com.jemmy.framework.component.user.User;
import com.jemmy.framework.controller.EntityKey;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class PackageAddress extends EntityKey {

    private String name;

    private String phone;

    // 城市
    private String city;

    // 区县
    private String district;

    // 省
    private String province;

    // 详细地址
    private String address;

    @OneToOne
    private User user;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
