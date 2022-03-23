package com.jemmy.framework.auto.config;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.admin.config.href.AdminHrefController;
import com.jemmy.framework.admin.config.href.entity.Menu;
import com.jemmy.framework.admin.config.href.entity.Page;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.page.entity.CreateData;
import com.jemmy.framework.auto.page.entity.CreateStepData;
import com.jemmy.framework.auto.page.AdminPage;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.auto.param.AutoParamType;
import com.jemmy.framework.auto.processor.FieldProcessor;
import com.jemmy.framework.auto.processor.ProcessorType;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.config.Config;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

@Component
public class ConfigProcessor {

    private static final Map<String, ConfigData> configs = new HashMap<>();

    @AutoAPI("Setting/Config")
    public static class _API {

        @Get(path = RequestPath.ADMIN)
        public Result<?> getCreateData(@AutoParam String name) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            if (!configs.containsKey(name)) {
                return Result.HTTP404();
            }

            ConfigData data = configs.get(name);

            List<CreateStepData<List<CreateData>>> page = data.getPage();

            Map<String, Object> map = new HashMap<>();

            // 写入选择器数值
            for (CreateStepData<List<CreateData>> d : page) {
                AdminPage.setSelectValue(d.getData());
            }

            map.put("api", "/AdminAPI/Setting/Config/Save");
            map.put("create", page);

            return Result.HTTP200().setData(map);
        }

        @Post(path = RequestPath.ADMIN)
        public Result<?> save(@AutoParam(type = AutoParamType.JSON) JemmyJson json, @AutoParam String configName) {
            return ConfigProcessor.save(json, configName);
        }

        @Get
        public Result<?> get(@AutoParam String name) throws IllegalAccessException {
            if (!configs.containsKey(name)) {
                return Result.of(ResultCode.HTTP400).putMessage("The current profile is not enabled");
            }

            ConfigData data = configs.get(name);

            if (!data.getOpenWeb()) {
                return Result.of(ResultCode.HTTP400).putMessage("Web access has not been enabled for the current configuration file");
            }

            Config<?> config = data.getConfig();

            return Result.of(ResultCode.HTTP200).setData(config.getJson());
        }

        @Get(path = RequestPath.ADMIN)
        public Result<?> getData(@AutoParam String name) {
            if (!configs.containsKey(name)) {
                return Result.of(ResultCode.HTTP400).putMessage("The current profile is not enabled");
            }

            ConfigData data = configs.get(name);

            Config<?> config = data.getConfig();

            Map<String, Object> json = config.getJson();

            // 循环遍历字段
            for (Field field : EntityUtils.getFields(config.getEntity().getClass())) {
                // 字段处理注解
                if (field.isAnnotationPresent(FieldProcessor.class)) {
                    FieldProcessor processor = field.getAnnotation(FieldProcessor.class);

                    // 价格字段处理
                    if (processor.type().equals(ProcessorType.PRICE)) {
                        Object price = json.get(field.getName());

                        if (price instanceof Integer) {
                            // 分转元
                            json.put(field.getName(), Double.valueOf((Integer) price) / 100);
                        }
                    }

                }
            }

            return Result.of(ResultCode.HTTP200).setData(json);
        }
    }

    public static void add(Object entity) {
        Class<?> clazz = entity.getClass();

        Config<?> config = new Config<>(clazz);

        List<CreateStepData<List<CreateData>>> page = new ArrayList<>();

        CreateStepData<List<CreateData>> general = new CreateStepData<>("通用数据", AdminPage.getCreateForm(EntityUtils.getFields(clazz), page));

        if (general.isExist() && general.getData().size() > 0) {
            page.add(general);
        }

        // Sort, big to small
        page.sort((arg0, arg1) -> (arg1.getSequence()).compareTo((arg0.getSequence())));

        String name = clazz.getSimpleName();

        AutoConfig autoConfig = clazz.getAnnotation(AutoConfig.class);

        // 是否开放 Web 访问权限
        Boolean openWeb = autoConfig.web();

        ConfigData data = new ConfigData(config, page, openWeb);

        configs.put(name, data);

        MenuType menuType = autoConfig.menu();

        // 菜单类型非 None
        if (!menuType.equals(MenuType.NONE)) {
            Menu menu = new Menu();

            menu.setIcon("config.svg");
            menu.setName(autoConfig.value());
            menu.setType("config");
            menu.setHref("/admin/#/form?type=config&name=" + name);

            Page p = new Page();

            p.setTitle(autoConfig.value());
            p.setType("config");
            p.setName(name);
            p.setCreateDataUrl("/Setting/Config/GetCreateData");
            p.setEditDataUrl("/Setting/Config/GetData");

            if (menuType.equals(MenuType.DATA))  {
                // 添加数据菜单
                AdminHrefController.addData(List.of(autoConfig.menus()), menu, p);
            } else {
                // 添加设置菜单
                AdminHrefController.addSetting(List.of(autoConfig.menus()), menu, p);
            }
        }
    }

    public static Map<String, Object> getMap(Class<?> clazz) {
        String name = clazz.getSimpleName();

        if (!configs.containsKey(name)) {
            try {
                return EntityUtils.toMap(clazz.getConstructor().newInstance());
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        ConfigData data = configs.get(name);

        return data.getConfig().getJson();
    }

    public static <T> T getEntity(Class<T> clazz) {
        String name = clazz.getSimpleName();

        if (!configs.containsKey(name)) {
            try {
                return clazz.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
//            throw new RuntimeException("The current profile is not enabled");
        }

        ConfigData data = configs.get(name);

        return (T) data.getConfig().getEntity();
    }

    public static Result<?> save(JemmyJson json, String configName) {
        if (!configs.containsKey(configName)) {
            return Result.of(ResultCode.HTTP400).putMessage("The current profile is not enabled");
        }

        ConfigData data = configs.get(configName);

        Config<?> config = data.getConfig();

        // 循环遍历字段
        for (Field field : EntityUtils.getFields(config.getEntity().getClass())) {
            // 字段处理注解
            if (field.isAnnotationPresent(FieldProcessor.class)) {
                FieldProcessor processor = field.getAnnotation(FieldProcessor.class);

                // 价格字段处理
                if (processor.type().equals(ProcessorType.PRICE)) {
                    Object price = json.get(field.getName());

                    if (price instanceof Integer) {
                        // 元转分
                        json.put(field.getName(), (double) (Integer) price * 100);
                    } else if (price instanceof Double) {
                        json.put(field.getName(), (Double) price * 100);
                    }
                }

            }
        }

        config.set(json);

        return Result.of(ResultCode.HTTP200);
    }
}
