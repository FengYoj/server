package com.jemmy.framework.component.user;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.api.annotation.WebFilter;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.component.resources.image.ResourceImage;

import javax.persistence.*;

@Entity
public class User extends com.jemmy.framework.connector.user.User {

    @FieldAttr("电话国家代码")
    private String code;

    @FieldAttr(value = "手机号码", unique = true, search = true)
    private String phone;

    @TableAttr(disable = true)
    private String avatarUrl;

    @OneToOne
    @ResourceAttr
    @FieldAttr("头像")
    private ResourceImage avatar;

    @FieldAttr("提供商")
    private String provider;

    @FieldAttr("注册IP")
    private String ip;

    @FieldAttr("国家")
    private String country;

    @FieldAttr("省份")
    private String province;

    @FieldAttr("城市")
    private String city;

    @FieldAttr("性别")
    @SelectField(fixed = {"未知", "男", "女"})
    private Integer gender;

    @WebFilter
    @FieldAttr(value = "公众号OpenID", unique = true)
    private String mpOpenid;

    @WebFilter
    @FieldAttr(value = "微信OpenID", unique = true)
    private String wxOpenid;

    @WebFilter
    @FieldAttr(value = "头条OpenID", unique = true)
    private String ttOpenid;

    @FieldAttr(value = "是否登录", filter = true)
    private Boolean login = false;

    @WebFilter
    private String token;

    public String getMpOpenid() {
        return mpOpenid;
    }

    public void setMpOpenid(String mpOpenid) {
        this.mpOpenid = mpOpenid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public ResourceImage getAvatar() {
        return avatar;
    }

    public void setAvatar(ResourceImage avatar) {
        this.avatar = avatar;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

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

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getWxOpenid() {
        return wxOpenid;
    }

    public void setWxOpenid(String wxOpenid) {
        this.wxOpenid = wxOpenid;
    }

    public String getTtOpenid() {
        return ttOpenid;
    }

    public void setTtOpenid(String ttOpenid) {
        this.ttOpenid = ttOpenid;
    }

    public Boolean getLogin() {
        return login;
    }

    public void setLogin(Boolean login) {
        this.login = login;
    }
}
