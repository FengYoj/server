package com.jemmy.framework.config;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;
import com.jemmy.framework.auto.page.annotation.StepAttr;

@AutoConfig(value = "服务支持", web = true, menu = MenuType.SETTING, menus = "系统设置")
public class ServiceSupport {

    @FieldAttr("域名")
    @StepAttr(title = "域名配置", prompt = "域名用于程序更新与反馈服务，正常情况下不建议修改，以免影响服务正常运行。")
    public static String domain = "http://www.shltds.com";

    @FieldAttr("程序异常反馈")
    @StepAttr(title = "反馈", prompt = "如出现程序异常将以匿名方式发生至服务域名，以帮助改进产品与服务。")
    public static Boolean exceptionFeedback = true;
}
