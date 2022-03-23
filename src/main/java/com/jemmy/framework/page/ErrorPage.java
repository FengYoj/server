package com.jemmy.framework.page;

import com.jemmy.framework.auto.config.ConfigProcessor;
import com.jemmy.framework.config.Setting;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("error")
public class ErrorPage {
    private final static String PATH = "error/error.html";

    interface ErrorEnum {
        int getCode();
        String getTitle();
    }

    enum ErrorEntity implements ErrorEnum {
        ERROR403(403, "403 no permission", "暂无权限查看该页面"),
        ERROR404(404, "404 not found", "该页无法正常显示"),
        ERROR500(500, "500 server error", "服务器跑去偷懒了");

        private final int code;
        private final String title;
        private final String content;

        ErrorEntity(int number, String title, String content) {
            this.code = number;
            this.title = title;
            this.content = content;
        }

        public int getCode() {
            return code;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }

    @RequestMapping(value = "{type}", method = { RequestMethod.GET, RequestMethod.POST })
    public static String error(@PathVariable Integer type, Model model) {
        if (model == null) {
            return PATH;
        }

        ErrorEntity errorEntity;

        try {
            errorEntity = ErrorEntity.valueOf("ERROR" + type);
        } catch (Exception ignored) {
            errorEntity = ErrorEntity.ERROR500;
        }

        model.addAttribute("setting", ConfigProcessor.getEntity(Setting.class));
        model.addAttribute("entity", errorEntity);

        return PATH;
    }

    public static String error(Integer type) {
        return error(type, null);
    }
}
