package com.jemmy.framework.component.version;

import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.StringUtils;
import com.jemmy.framework.utils.result.Result;

@AutoAPI
public class VersionController extends JpaController<Version, VersionRepository> {

    public static enum TYPE {
        /** 修订补丁 */
        PATCH("PATCH"),
        /** 特性升级 */
        FEATURE("FEATURE"),
        /** 版本升级 */
        VERSION("VERSION");

        TYPE(String name) {
            this.name = name;
        }

        private final String name;

        public String getName() {
            return name;
        }
    }

    /**
     * 版本号自增
     * @param name 标识符
     * @param type 自增类型
     */
    public void increase(String name, TYPE type) {
        Version version = repository.findByName(name);

        if (version == null) {
            return;
        }

        increase(version, type);
    }

    /**
     * 版本号自增
     * @param v 版本实体
     * @param type 自增类型
     */
    public void increase(Version v, TYPE type) {
        String version = v.getVersion();

        switch (type) {
            case PATCH:
                version = version.replaceFirst("(\\.)(\\d+)$", "$1" + (Integer.parseInt(StringUtils.getMatcher("\\.(\\d+)$", version)) + 1));
                break;
            case FEATURE:
                version = version.replaceFirst("(\\.)(\\d+)(\\.\\d+)$", "$1" + (Integer.parseInt(StringUtils.getMatcher("\\.(\\d+)\\.", version)) + 1) + ".0");
                break;
            case VERSION:
                version = version.replaceFirst("^(\\d+)(\\..*)$", (Integer.parseInt(StringUtils.getMatcher("^(\\d+)\\.", version)) + 1) + ".0.0");
        }

        // 写入版本号
        v.setVersion(version);

        // 保存
        controller.save(v);
    }

    /**
     * 创建版本实体
     * @param name 标识符
     * @param title 标题名称
     */
    public Result<String> create(String name, String title) {
        return controller.save(new Version("1.0.0", name, title));
    }
}
