package com.jemmy.framework.component.language;

import com.jemmy.framework.component.language.language_sign.LanguageSign;
import com.jemmy.framework.controller.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LanguageRepository extends JpaRepository<Language> {
    @Query("select l from Language l where l.sign.sign = ?1 and l.sign.terminal = ?2")
    Language findBySignAndTerminal(String sign, String terminal);

    List<Language> findAllBySign(LanguageSign languageSign);

    List<Language> findAllBySignTerminal(String terminal);
}
