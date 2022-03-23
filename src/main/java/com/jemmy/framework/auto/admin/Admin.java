package com.jemmy.framework.auto.admin;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.annotation.EntityAttr;
import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.api.annotation.Delete;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.page.entity.CreateData;
import com.jemmy.framework.auto.page.entity.CreateStepData;
import com.jemmy.framework.auto.page.entity.GroupData;
import com.jemmy.framework.auto.page.entity.TableData;
import com.jemmy.framework.auto.page.operating.DefaultOperating;
import com.jemmy.framework.auto.page.operating.Operating;
import com.jemmy.framework.auto.page.operating.TableOperating;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.password.PasswordField;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.auto.page.AdminPage;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.auto.processor.FieldProcessor;
import com.jemmy.framework.auto.processor.ProcessorType;
import com.jemmy.framework.component.password.Password;
import com.jemmy.framework.component.password.PasswordController;
import com.jemmy.framework.controller.EntityInfo;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.object.Filter;
import com.jemmy.framework.registrar.ControllerRegistrar;
import com.jemmy.framework.utils.*;
import com.jemmy.framework.utils.result.Result;
import com.jemmy.framework.utils.result.ResultCode;
import com.jemmy.framework.utils.value.StringListValue;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Admin<T extends EntityKey, C extends JpaController<T, ?>> {

    private final String api;

    private final Class<T> entity;

    private final List<CreateStepData<List<CreateData>>> create;

    private List<TableData> table;

    protected final C jpaController;

    private final List<String> pages;

    // 操作模块
    private final List<Operating> operatings;

    private String name = null;

    private PasswordController passwordController = SpringBeanUtils.getBean(PasswordController.class);

    public Admin() {
        this.jpaController = ClassUtils.getBean(this.getClass(), 1);

        entity = jpaController.getEntity();

        api = "/AdminAPI/" + entity.getSimpleName();

        AutoAdmin autoAdmin = AnnotationUtils.findAnnotation(this.getClass(), AutoAdmin.class);

        if (autoAdmin != null) {
            pages = List.of(autoAdmin.pages().getName());

            if (StringUtils.isExist(autoAdmin.value())) {
                name = autoAdmin.value();
            }
        } else {
            pages = new ArrayList<>();

            pages.add("table");
            pages.add("create");
            pages.add("edit");
        }

        if (name == null) {
            name = entity.getSimpleName();
        }

        List<CreateStepData<List<CreateData>>> create = new ArrayList<>();

        CreateStepData<List<CreateData>> general = new CreateStepData<>("通用数据", AdminPage.getCreateForm(EntityUtils.getFields(entity), create));

        if (general.isExist() && general.getData().size() > 0) {
            create.add(general);
        }

        // Sort, big to small
        create.sort((arg0, arg1) -> (arg1.getSequence()).compareTo((arg0.getSequence())));

        this.create = create;

        if (pages.contains("table")) {
            this.table = AdminPage.getTable(EntityUtils.getFields(entity));
        }

        this.getName();

        List<Operating> operatings = new ArrayList<>();

        if (autoAdmin != null) {
            Class<? extends TableOperating> operating = autoAdmin.operating();

            try {
                Object entity = operating.getConstructor().newInstance();

                for (Method method : operating.getMethods()) {
                    if (Operating.class.isAssignableFrom(method.getReturnType())) {
                        operatings.add((Operating) method.invoke(entity));
                    }
                }

                for (Field field : EntityUtils.getFields(operating)) {
                    field.setAccessible(true);

                    Object value = field.get(entity);

                    if (Operating.class.isAssignableFrom(value.getClass())) {
                        operatings.add((Operating) value);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            DefaultOperating operating = new DefaultOperating();
            operatings.add(operating.delete());
            operatings.add(operating.edit());
        }

        this.operatings = operatings;
    }

    @Get(path = RequestPath.ADMIN)
    public void exportAllData(HttpServletResponse response) throws IOException {
        HSSFWorkbook wb = jpaController.export.toExcel();

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream os = response.getOutputStream();
        response.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", EntityUtils.getEntityTitle(entity)));
        wb.write(os);
        os.flush();
        os.close();
    }

    @Post(path = RequestPath.ADMIN)
    public void exportDataByPage(@AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer size, @AutoParam(required = false) String search, @AutoParam(required = false) List<Filter> filter, HttpServletResponse response) throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Result<Page<T>> result = getPage(page, size, search, filter);

        if (result.isBlank()) {
            throw new RuntimeException(result.getMessage());
        }

        HSSFWorkbook wb = jpaController.export.toExcel(result.getData().getContent());

        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        OutputStream os = response.getOutputStream();
        response.setHeader("Content-disposition", String.format("attachment;filename=%s.xls", EntityUtils.getEntityTitle(entity)));
        wb.write(os);
        os.flush();
        os.close();
    }

    @Get(path = RequestPath.ADMIN)
    public Result<?> getTableData() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> map = new HashMap<>();

        map.put("pages", pages);
        map.put("api", api);
        map.put("table", table);
        map.put("group", getGroupData());
        map.put("operatings", operatings);

        return Result.HTTP200().setData(map);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<?> getCreateData() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Map<String, Object> map = new HashMap<>();

        for (CreateStepData<List<CreateData>> data : create) {
            AdminPage.setSelectValue(data.getData());
        }

        map.put("api", api + "/Save");
        map.put("create", create);

        return Result.HTTP200().setData(map);
    }

    @Post(path = RequestPath.ADMIN)
    public Result<String> save(@AutoParam T entity) {

        try {

            // 循环遍历字段
            for (Field field : EntityUtils.getFields(entity.getClass())) {

                field.setAccessible(true);

                // 字段处理注解
                if (field.isAnnotationPresent(FieldProcessor.class)) {
                    FieldProcessor processor = field.getAnnotation(FieldProcessor.class);

                    // 价格字段处理
                    if (processor.type().equals(ProcessorType.PRICE)) {
                        Object price = field.get(entity);

                        if (price instanceof Integer) {
                            // 元转分
                            field.set(entity, (Integer) price * 100);
                        } else if (price instanceof Double) {
                            field.set(entity, (Double) price * 100);
                        }
                    }
                }

                else if (field.isAnnotationPresent(PasswordField.class) || Password.class.isAssignableFrom(field.getType())) {
                    if (Password.class.isAssignableFrom(field.getType())) {
                        Password password = (Password) field.get(entity);
                        // 修改密码
                        passwordController.change(password, password.getValue());
                    }
                }
            }

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return jpaController.controller.save(entity);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<?> findEntity(@AutoParam String uuid) throws IllegalAccessException {
        return jpaController.controller.findByUuid(uuid);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<?> findProcessEntity(@AutoParam String uuid) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Result<T> result = jpaController.controller.findByUuid(uuid);

        if (result.isBlank()) {
            return result;
        }

        return Result.HTTP200().setData(EntityUtils.processField(result.getData()));
    }

    @Post(path = RequestPath.ADMIN)
    public Result<PageEntity> findAllToPage(@AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer size, @AutoParam(required = false) String search, @AutoParam(required = false) List<Filter> filter) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

        Result<Page<T>> result = getPage(page, size, search, filter);

        if (result.isBlank()) {
            return result.toObject();
        }

        Page<T> pageEntity = result.getData();

        return Result.<PageEntity>of(ResultCode.HTTP200).setData(new PageEntity(pageEntity, EntityUtils.processField(pageEntity.getContent())));
    }

    private Result<Page<T>> getPage(Integer page, Integer size, String search, List<Filter> filter) {
        Result<Page<T>> result;

        if (StringUtils.isExist(search) && filter != null) {
            result = jpaController.controller.searchToPage(page, size, search, filter);
        }

        else if (StringUtils.isExist(search)) {
            result = jpaController.controller.searchToPage(page, size, search);
        }

        else if (filter != null) {
            result = jpaController.controller.findAllByFilterToPage(page, size, filter);
        }

        else {
            result = jpaController.controller.findAllToPage(page, size);
        }

        return result;
    }

    @Delete(path = RequestPath.ADMIN)
    public Result<?> delete(@AutoParam String i) {
        return jpaController.controller.deleteByUuid(i);
    }

    @Post(path = RequestPath.ADMIN)
    public Result<?> changeFieldValue(@AutoParam T entity, @AutoParam String field, @AutoParam Object value) throws IllegalAccessException {
        Field f = EntityUtils.getField(entity, field);

        if (f == null) {
            return Result.HTTP400().putMessage(field + " field does not exist");
        }

        if (!f.getType().isAssignableFrom(value.getClass())) {
            return Result.HTTP400().putMessage(field + " field types are not the same");
        }

        f.setAccessible(true);
        f.set(entity, value);

        return jpaController.controller.update(entity);
    }

    @Get(path = RequestPath.ADMIN)
    public Result<List<EntityInfo>> getEntityInfo() {
        List<EntityInfo> res = new ArrayList<>();

        for (Field field : EntityUtils.getFields(this.entity)) {
            EntityInfo info = new EntityInfo();

            info.setName(field.getName());
            info.setType(AdminPage.getType(field, FieldType.Auto));

            if (field.isAnnotationPresent(FieldAttr.class)) {
                FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

                info.setTitle(fieldAttr.value());
            }

            res.add(info);
        }

        return Result.<List<EntityInfo>>HTTP200().setData(res);
    }

    private List<GroupData> getGroupData() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        List<GroupData> list = new ArrayList<>();

        for (Field field : EntityUtils.getFields(entity)) {
            if (field.isAnnotationPresent(FieldAttr.class)) {

                FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

                if (fieldAttr.filter()) {
                    GroupData group = new GroupData();

                    List<Object> data = new ArrayList<>();

                    Class<?> clazz = field.getType();

                    // Is it an entity class
                    if (EntityKey.class.isAssignableFrom(clazz) && !field.isAnnotationPresent(SelectField.class)) {

                        Class<? extends EntityKey> c = (Class<? extends EntityKey>) clazz;

                        JpaController<?, ?> controller = ControllerRegistrar.get(c);

                        for (EntityKey t : controller.controller.findAll().getData()) {
                            Map<String, Object> map = new HashMap<>() {
                                {
                                    put("title", EntityUtils.getTitleValue(t));
                                    put("value", t.getUuid());
                                }
                            };

                            data.add(map);
                            group.setName(field.getName() + "_uuid");
                        }
                    }

                    else if (field.isAnnotationPresent(SelectField.class)) {
                        SelectField select = field.getAnnotation(SelectField.class);

                        if (EntityKey.class.isAssignableFrom(field.getType())) {
                            // 获取控制器 Bean
                            JpaController<?, ?> jpaController = ControllerRegistrar.get(field.getType());

                            // 判断 select 字段 filter 值是否为存在
                            if (StringUtils.isExist(select.filter())) {

                                for (EntityKey t : jpaController.controller.findCustomize(select.filter()).getData()) {
                                    Map<String, Object> map = new HashMap<>() {
                                        {
                                            put("title", StringUtils.isExist(select.field()) ? EntityUtils.getVal(select.field(), t) : EntityUtils.getTitleValue(t));
                                            put("value", t.getUuid());
                                        }
                                    };

                                    data.add(map);
                                }
                            } else {
                                for (EntityKey t : jpaController.controller.findAll().getData()) {
                                    Map<String, Object> map = new HashMap<>() {
                                        {
                                            put("title", StringUtils.isExist(select.field()) ? EntityUtils.getVal(select.field(), t) : EntityUtils.getTitleValue(t));
                                            put("value", t.getUuid());
                                        }
                                    };

                                    data.add(map);
                                }
                            }

                            group.setName(field.getName() + "_uuid");
                        } else {
                            String[] v;

                            if (!select.variable().getName().equals(StringListValue.class.getName())) {
                                Class<? extends StringListValue> variable = select.variable();

                                StringListValue stringListValue = variable.getConstructor().newInstance();

                                v = stringListValue.getContent();
                            } else {
                                v = select.fixed();
                            }

                            int i = 0;
                            for (; i < v.length; i++) {
                                Map<String, Object> item = new HashMap<>();

                                String[] split = v[i].split(":");

                                if (split.length > 1) {
                                    item.put("title", split[1]);
                                    item.put("value", split[0]);
                                } else {
                                    item.put("title", split[0]);
                                    item.put("value", i);
                                }

                                data.add(item);
                            }
                        }
                    } else if (BooleanUtils.is(field.getType())) {
                        data.add(new HashMap<>() { { put("title", "是"); put("value", true); } });
                        data.add(new HashMap<>() { { put("title", "否"); put("value", false); } });
                    } else {
                        List<?> groups = jpaController.controller.findFieldGroup(field.getName());

                        for (Object v : groups) {
                            Map<String, Object> map = new HashMap<>() {
                                {
                                    put("title", v);
                                    put("value", v);
                                }
                            };

                            data.add(map);
                        }
                    }

                    if (StringUtils.isBlank(group.getName())) {
                        group.setName(field.getName());
                    }

                    group.setData(data);
                    group.setTitle(StringUtils.defaults(fieldAttr.value(), field.getName()));

                    list.add(group);
                }
            }
        }

        return list;
    }

    private String setBooleanName(Boolean is) {
        return is ? "是" : "否";
    }

    public Class<T> getEntity() {
        return jpaController.getEntity();
    }

    private void getName() {
        if (entity.isAnnotationPresent(EntityAttr.class)) {
            name = entity.getAnnotation(EntityAttr.class).value();
        }

        // If empty, set default value
        if (StringUtils.isBlank(name)) {
            name = entity.getSimpleName();
        }
    }
}
