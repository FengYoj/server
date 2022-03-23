package com.jemmy.framework.admin.controller;

import com.jemmy.framework.admin.dao.Exception;
import com.jemmy.framework.admin.repository.ExceptionRepository;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.admin.AutoAdmin;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.controller.JpaRestController;
import com.jemmy.framework.utils.NumberUtils;

@JpaRestController
public class ExceptionController extends JpaController<Exception, ExceptionRepository> {

    @AutoAdmin(pages = AutoAdmin.Page.Table)
    public static class _ADMIN extends Admin<Exception, ExceptionController> {}

    public Integer getTodayNumber() {
        return NumberUtils.defaults(repository.findTodayNumber(), 0);
    }

    public Integer getYesterdayNumber() {
        return NumberUtils.defaults(repository.findYesterdayNumber(), 0);
    }

}
