package com.jemmy.framework.component.language.language_sign;

import com.jemmy.framework.controller.JpaRepository;

public interface LanguageSignRepository extends JpaRepository<LanguageSign> {

    LanguageSign findBySignAndTerminal(String sign, String terminal);

}
