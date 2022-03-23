package com.jemmy.config;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;
import com.jemmy.framework.auto.config.PrivateConfig;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.component.resources.image.ResourceImage;

@AutoConfig(value = "微信配置", web = true, menu = MenuType.SETTING)
public class WxConfig {

    @PrivateConfig
    @StepAttr(name = "we", title = "小程序配置")
    @FieldAttr("小程序 App ID")
    public static String weAppId = "";

    @PrivateConfig
    @StepAttr(mappedBy = "we")
    @FieldAttr("小程序 App Secret")
    public static String weAppSecret = "";

    @StepAttr(name = "mp", title = "公众号配置")
    @FieldAttr("公众号 App ID")
    public static String mpAppId = "";

    @PrivateConfig
    @StepAttr(mappedBy = "mp")
    @FieldAttr("公众号 Secret")
    public static String mpSecret = "";

    @PrivateConfig
    @StepAttr(name = "pay", title = "微信支付配置")
    @FieldAttr("商户号")
    public static String mchId = "";

    @PrivateConfig
    @StepAttr(mappedBy = "pay")
    @FieldAttr("商户 Key")
    public static String mchKey = "";

    @PrivateConfig
    @StepAttr(mappedBy = "pay")
    @FieldAttr("证书文件路径")
    public static String certificate;

    @PrivateConfig
    @StepAttr(mappedBy = "pay")
    @FieldAttr("证书序列号")
    public static String certificateSerialNumber = "";

    @StepAttr(name = "share", title = "小程序分享配置")
    @FieldAttr("分享图片")
    @ResourceAttr
    public static ResourceImage sharePhoto;

    @StepAttr(mappedBy = "share")
    @FieldAttr("分享标题")
    public static String shareTitle;

    @StepAttr(name = "instead", title = "代付配置")
    @FieldAttr("代付分享图片")
    @ResourceAttr
    public static ResourceImage insteadPoster;

    @StepAttr(mappedBy = "instead")
    @FieldAttr("代付分享标题")
    public static String insteadTitle;
}
