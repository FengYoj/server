package com.jemmy.config;

import com.jemmy.framework.utils.value.StringListValue;

public interface LanguageConfig {

    /** 终端 */
    class TERMINAL extends StringListValue {
        public TERMINAL() {
            super(new String[]{
                    "all:全终端",
                    "server_admin:管理端"
            });
        }
    }

    /** 新版语言包 */
    boolean LATEST = true;
}
