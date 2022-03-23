package com.jemmy.config;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.config.AutoConfig;
import com.jemmy.framework.auto.config.MenuType;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;

@AutoConfig(value = "文件路径配置", menu = MenuType.SETTING)
public class PathConfig {
    @StepAttr(name = "uploadToOss", title = "是否上传资源文件至阿里云 OSS")
    @FieldAttr("开启阿里云 OSS")
    public static Boolean uploadToOss = false;

    @FieldAttr("配置文件路径")
    @CreateAttr(disable = true)
    public static String config = "../config/";

    @FieldAttr("上传文件路径")
    public static String upload = "../upload/";

    @FieldAttr("临时文件路径")
    public static String temp = "../temp/";

    @FieldAttr(value = "管理端源路径", empty = false)
    public static String admin = "/project/server/admin";

    @CreateAttr(disable = true)
    public static String uploadImage = upload + "image/";

    @CreateAttr(disable = true)
    public static String uploadVideo = upload + "video/";

    @CreateAttr(disable = true)
    public static String uploadAudio = upload + "audio/";

    @CreateAttr(disable = true)
    public static String uploadFile = upload + "file/";

    @CreateAttr(disable = true)
    public static String mp_language = config + "language/mp/";

    @CreateAttr(disable = true)
    public static String pc_language = config + "language/pc/";

    @CreateAttr(disable = true)
    public static String pc_app_language = config + "language/pc_app/";

    @CreateAttr(disable = true)
    public static String coach_language = config + "language/coach/";

    @CreateAttr(disable = true)
    public static String server_admin_language = config + "language/server_admin/";
}
