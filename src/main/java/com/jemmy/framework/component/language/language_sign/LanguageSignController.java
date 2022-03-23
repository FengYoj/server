package com.jemmy.framework.component.language.language_sign;

import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.result.Result;

@AutoAPI
public class LanguageSignController extends JpaController<LanguageSign, LanguageSignRepository> {

    @AutoAPI
    public static class _ADMIN extends Admin<LanguageSign, LanguageSignController> {}

    public Result<LanguageSign> findBySignAndTerminal(String sign, String terminal) {
        return controller.examineFind(repository.findBySignAndTerminal(sign, terminal));
    }
}
