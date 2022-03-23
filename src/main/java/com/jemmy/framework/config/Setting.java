package com.jemmy.framework.config;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.ConfigUtils;
import com.jemmy.framework.auto.config.MenuType;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.component.resources.image.ResourceImage;
import lombok.Data;

@Data
@AutoConfig(value = "基本配置", web = true, menu = MenuType.SETTING, menus = "系统设置")
public class Setting extends ConfigUtils {

    @FieldAttr("名称")
    public static String name;

    @FieldAttr("图标")
    @StepAttr(name = "icon", title = "上传图标")
    @ResourceAttr
    public static ResourceImage icon;

    @FieldAttr(value = "主域名", empty = false)
    public static String DOMAIN = "http://localhost";
}
