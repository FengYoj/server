package com.jemmy.framework.config;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.type.select.SelectField;

@AutoConfig(value = "安全配置", menu = MenuType.SETTING, menus = "系统设置")
public class SafetyConfig {

    @StepAttr(name = "block", title = "访问安全启动开关")
    @FieldAttr("访问限制")
    public static Boolean block = true;

    @StepAttr(name = "block_config", title = "访问安全配置")
    @FieldAttr("访问频率限制（单位：次/每分钟）")
    public static Long frequency = 500L;

    @StepAttr(mappedBy = "block_config")
    @FieldAttr("限制执行")
    @SelectField(fixed = { "block:加入限制名单", "sleep:访问休眠（五分钟）", "forward:推送消息" })
    public static String execute = "sleep";

}
