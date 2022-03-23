package com.jemmy.framework.component.language;

import com.jemmy.config.LanguageConfig;
import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.language.language_sign.LanguageSign;
import com.jemmy.framework.component.language.language_sign.LanguageSignController;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.controller.JpaRestController;
import com.jemmy.framework.utils.LockMap;
import com.jemmy.framework.utils.ParamUtils;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 语言包 2.0
 *
 * 后台兼容 1.0 版本，通过修改 LanguageConfig 接口中的 LATEST 属性进行修改
 *
 * LATEST = true = 2.0
 * LATEST = false = 1.0
 *
 * */

@AutoAPI
@JpaRestController
public class LanguageController extends JpaController<Language, LanguageRepository> {

    private final LockMap<String> keyLock = LockMap.forLargeKeySet();

    private final LanguageSignController languageSignController;

    // 缓存语言包
    private final Map<String, JemmyJson> cache = new HashMap<>();

    LanguageController(LanguageSignController languageSignController) {
        this.languageSignController = languageSignController;
    }

    @AutoAPI
    public class _ADMIN extends Admin<Language, LanguageController> {

        @Override
        @Post(path = RequestPath.ADMIN)
        public Result<String> save(@AutoParam Language language) {

            JemmyJson data;

            try {
                data = JemmyJson.toJemmyJson(language.getData());
            } catch (Exception e) {
                return Result.HTTP400("数据结构必须为 JSON 格式");
            }

            LanguageSign sign = language.getSign();

            // 缓存更新后的语言包
            cache.put(sign.getSign() + "_" + sign.getTerminal(), data);

            return super.save(language);
        }

    }

    @Get
    public Result<JemmyJson> findLanguageBySign(@AutoParam String sign, @AutoParam String terminal) {
        JemmyJson data;

        String key = sign + "_" + terminal;

        if (cache.containsKey(key)) {
            data = cache.get(key);
        } else {
            Language language = repository.findBySignAndTerminal(sign, terminal);

            if (language == null) {
                data = cache.get("cn_" + terminal);
            } else {
                data = JemmyJson.toJemmyJson(language.getData());

                // 缓存
                cache.put(key, data);
            }
        }

        if (data == null) {
            data = new JemmyJson();
        }

        return new Result<JemmyJson>(ResultCode.HTTP200).setData(data);
    }

    /**
     * 更新语言包
     * @param page 所属页面（仅 1.0 版本）
     * @param terminal 所属终端
     * @param data Json 数据
     */
    @Post
    public Result<?> updateLanguage(@AutoParam(required = false) String page, @AutoParam String terminal, @AutoParam JemmyJson data) {
        if (!LanguageConfig.LATEST) {
            return pastUpdateLanguage(page, terminal, data);
        }

        List<Language> languages = repository.findAllBySignTerminal(terminal);

        if (!languages.isEmpty()) {
            for (Language language : languages) {
                JemmyJson json = JemmyJson.toJemmyJson(language.getData());

                data.forEach((k, v) -> {
                    if (!json.containsKey(k)) {
                        json.put(k, v);
                    }
                });

                language.setData(json.toJSONString());
            }

            // 保存
            repository.saveAll(languages);
        }

        return Result.HTTP200();
    }

    /** 旧版本 */
    public Result<?> pastUpdateLanguage(String page, String terminal, JemmyJson data) {

        ParamUtils.check("page", page);

        try {
            keyLock.lock("UpdateLanguage");

            Language language = repository.findBySignAndTerminal("cn", terminal);

            if (language == null) {
                Result<LanguageSign> languageSignResult = languageSignController.findBySignAndTerminal("cn", terminal);

                LanguageSign languageSign;

                if (languageSignResult.isNormal()) {
                    languageSign = languageSignResult.getData();
                } else {
                    languageSign = new LanguageSign(terminal, "cn", "中文");
                    languageSignController.controller.save(languageSign);
                }

                JemmyJson json = new JemmyJson();

                json.put(page, data);

                language = new Language(languageSign, json.toJSONString());
            } else {
                JemmyJson json = JemmyJson.toJemmyJson(language.getData());

                json.put(page, data);

                language.setData(json.toJSONString());

                List<Language> languages = repository.findAllBySign(language.getSign());

                for (Language l : languages) {

                }
            }

            return controller.save(language);

        } catch (InterruptedException e) {
            return new Result<>(ResultCode.HTTP500).setMessage("线程锁异常").putMessage(e.getMessage());
        } finally {
            keyLock.unlock("UpdateLanguage");
        }
    }
}
