package com.jemmy.framework.auto.page;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.StepAttr;
import com.jemmy.framework.auto.page.annotation.field.CreateAttr;
import com.jemmy.framework.auto.page.annotation.field.EditorField;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.auto.page.config.ResourceConfig;
import com.jemmy.framework.auto.page.config.SubclassConfig;
import com.jemmy.framework.auto.page.entity.*;
import com.jemmy.framework.auto.page.type.FieldType;
import com.jemmy.framework.auto.page.type.label.LabelConfig;
import com.jemmy.framework.auto.page.type.label.LabelField;
import com.jemmy.framework.auto.page.type.password.PasswordConfig;
import com.jemmy.framework.auto.page.type.password.PasswordField;
import com.jemmy.framework.auto.page.type.select.SelectConfig;
import com.jemmy.framework.auto.page.type.select.SelectField;
import com.jemmy.framework.auto.page.type.upload.UploadConfig;
import com.jemmy.framework.component.location.Address;
import com.jemmy.framework.component.password.Password;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.component.resources.ResourceAttr;
import com.jemmy.framework.component.resources.audio.ResourceAudio;
import com.jemmy.framework.component.resources.image.ResourceImage;
import com.jemmy.framework.component.resources.video.ResourceVideo;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.*;
import com.jemmy.framework.utils.value.StringListValue;

import javax.persistence.OneToMany;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminPage {

    public static List<CreateData> getCreateForm(List<Field> fields, List<CreateStepData<List<CreateData>>> step) {
        List<CreateData> create = new ArrayList<>();

        for (Field field: fields) {

            if (step != null && field.isAnnotationPresent(StepAttr.class)) {
                appendStep(field, field.getAnnotation(StepAttr.class), step);
                continue;
            }

            CreateData createData = getCreateData(field);

            if (createData != null) {

                switch (createData.getType()) {
                    case List:
                        Class<?> generic = ListUtils.getGenericType(field);

                        FieldAttr f = field.getAnnotation(FieldAttr.class);

                        if (step == null) {
                            continue;
                        }

                        step.add(new CreateStepData<>(StringUtils.defaults(f == null ? "" : f.value(), createData.getName()), getCreateForm(EntityUtils.getFields(generic), null), createData.getField(), createData.getField(), true));

                        break;
                    case Upload:
                        if (step != null) {
                            CreateStepData<List<CreateData>> stepData = null;

                            for (CreateStepData<List<CreateData>> createStepData : step) {
                                if (createStepData.getName().equals("Resource")) {
                                    stepData = createStepData;
                                }
                            }

                            if (stepData == null) {
                                step.add(new CreateStepData<>("资源文件", new ArrayList<>() {{ add(createData); }}, "Resource", createData.getField()));
                            } else {
                                stepData.getData().add(createData);
                            }

                            break;
                        }
                    case Entity:
                        if (step == null) {
                            create.addAll(getCreateForm(EntityUtils.getFields(field.getType()), null));
                        } else {
                            FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

                            step.add(new CreateStepData<>(StringUtils.defaults(fieldAttr == null ? "" : fieldAttr.value(), createData.getName()), getCreateForm(EntityUtils.getFields(field.getType()), null), createData.getField(), createData.getField()));

                            continue;
                        }
                    case Password:
                        if (step != null) {

                            step.add(new CreateStepData<>("密码", new ArrayList<>() {{ add(createData); }}, "Resource", createData.getField(), CreateStepType.PASSWORD));

                            break;
                        }
                    default:
                        create.add(createData);
                }
            }
        }

        // 排序，从大到小
        create.sort((arg0, arg1) -> (arg1.getSequence()).compareTo((arg0.getSequence())));

        return create;
    }

    public static CreateData getCreateData(Field field) {

        FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

        String name = StringUtils.defaults(fieldAttr == null ? "" : fieldAttr.value(), field.getName());

        CreateData createData;

        if (fieldAttr == null) {
            createData = new CreateData(field.getName(), name, 0);
        } else {
            createData = new CreateData(field.getName(), name, fieldAttr.sequence());
        }

        if (field.isAnnotationPresent(CreateAttr.class)) {
            CreateAttr createField = field.getAnnotation(CreateAttr.class);

            // 不显示，返回空
            if (createField.disable()) {
                return null;
            }

            createData.setField(createField.field().isEmpty() ? field.getName() : createField.field());
            createData.setLength(createField.length());
            createData.setPlaceholder(createField.placeholder());
            createData.setType(getType(field, createField.type()));
            createData.setWhere(createField.where());
        } else {
            createData.setField(field.getName());
            createData.setLength(255);
            createData.setType(getType(field, FieldType.Auto));
        }

        // 判断是否为必选项
        createData.setRequired(fieldAttr != null && !fieldAttr.empty());

        if (field.isAnnotationPresent(SelectField.class)) {
            SelectField selectField = field.getAnnotation(SelectField.class);

            SelectConfig select = new SelectConfig();
            select.setSelectField(selectField);
            createData.setType(FieldType.Select);

            // 数组类型
            if (List.class.isAssignableFrom(field.getType())) {
                select.setMultiple(true);
                select.setType(ListUtils.getGenericType(field));
            } else {
                select.setType(field.getType());
            }

            select.setController(EntityKey.class.isAssignableFrom(select.getType()));
            createData.setSelectConfig(select);
        }

        else if (field.isAnnotationPresent(ResourceAttr.class)) {
            ResourceAttr attr = field.getAnnotation(ResourceAttr.class);

            UploadConfig upload = new UploadConfig();

            upload.setAccept(attr.accept());

            // 属性类型为字符串类型
            if (String.class.isAssignableFrom(field.getType())) {
                upload.setType(attr.type());
            } else {
                // 判断字段是否为 List 类型，如果是则开启多文件上传
                upload.setMulti(List.class.isAssignableFrom(field.getType()));
                // 上传类型
                upload.setType(getResourceType(field));
            }

            createData.setType(FieldType.Upload);
            createData.setUploadConfig(upload);
        }

        else if (field.isAnnotationPresent(EditorField.class)) {
//            EditorField editorField = field.getAnnotation(EditorField.class);

            createData.setType(FieldType.Editor);
//            createData.setEditorConfig(new EditorConfig());
        }

        else if (field.isAnnotationPresent(LabelField.class)) {
            LabelField labelField = field.getAnnotation(LabelField.class);

            LabelConfig config = new LabelConfig();
            config.setType(labelField.type().getType());

            createData.setLabelConfig(config);
            createData.setType(FieldType.Label);
        }

        else if (field.isAnnotationPresent(PasswordField.class)) {
            createData.setType(FieldType.Password);
            createData.setPasswordConfig(new PasswordConfig(field.getAnnotation(PasswordField.class)));
        }

        if (createData.getType().equals(FieldType.Password)) {
            PasswordConfig config = createData.getPasswordConfig();

            if (config == null) {
                config = new PasswordConfig();
            }

            config.setEntity(Password.class.isAssignableFrom(field.getType()));

            createData.setPasswordConfig(config);
        }

        return createData;
    }

    /**
     * Append step list
     * @param field field
     * @param stepAttr stepAttr annotation
     * @param step step list
     */
    public static void appendStep(Field field, StepAttr stepAttr, List<CreateStepData<List<CreateData>>> step) {
        if (StringUtils.isExist(stepAttr.name()) || StringUtils.isExist(stepAttr.mappedBy())) {
            for (CreateStepData<List<CreateData>> createStepData : step) {
                String name = createStepData.getName();

                // Determine if there is a corresponding list
                if (name.equals(stepAttr.name()) || name.equals(stepAttr.mappedBy())) {

                    // Add to list
                    createStepData.getData().add(getCreateData(field));

                    // Sort, big to small
                    createStepData.getData().sort((arg0, arg1) -> (arg1.getSequence()).compareTo((arg0.getSequence())));

                    // Do not go down
                    return;
                }
            }
        }

        // New list
        List<CreateData> list = new ArrayList<>();

        // Add data to list
        list.add(getCreateData(field));

        // Add list to step
        CreateStepData<List<CreateData>> createStepData = new CreateStepData<>(stepAttr.title(), list, StringUtils.defaults(stepAttr.name(), field.getName()));
        createStepData.setWhere(stepAttr.where());
        createStepData.setSequence(stepAttr.sequence());
        createStepData.setPrompt(stepAttr.prompt());

        step.add(createStepData);
    }

    public static FieldType getType(Field field, FieldType type) {
        if (field.isAnnotationPresent(FieldAttr.class)) {
            FieldType fieldType = field.getAnnotation(FieldAttr.class).type();

            if (!fieldType.equals(FieldType.Auto)) {
                return fieldType;
            }
        }

        Class<?> t = field.getType();

        // If the specified type exists
        if (!type.equals(FieldType.Auto)) {
            return type;
        }

        if (t.equals(Integer.class) || t.equals(int.class) || t.equals(Long.class) || t.equals(long.class) || t.equals(Double.class) || t.equals(double.class)) {
            return FieldType.Number;
        }

        else if (t.equals(Boolean.class) || t.equals(boolean.class)) {
            return FieldType.Switch;
        }

        else if (List.class.isAssignableFrom(t)) {
            return FieldType.List;
        }

        else if (Address.class.isAssignableFrom(t)) {
            return FieldType.Map;
        }

        else if (Password.class.isAssignableFrom(t)) {
            return FieldType.Password;
        }

        else if (t.isEnum()) {
            return FieldType.ENUM;
        }

        else if (!ClassUtils.isBaseType(t)) {
            return FieldType.Entity;
        }

        // Default input
        return FieldType.Input;
    }

    public static List<TableData> getTable(List<Field> fields) {
        List<TableData> table = new ArrayList<>();

        for (Field field: fields) {
            FieldAttr fieldAttr = field.getAnnotation(FieldAttr.class);

            String name = StringUtils.defaults(fieldAttr == null ? "" : fieldAttr.value(), field.getName());

            TableData tableData = new TableData(AdminPage.getType(field, FieldType.Auto), field.getName(), name);

            // 处理表格字段
            if (field.isAnnotationPresent(TableAttr.class)) {
                TableAttr tableField = field.getAnnotation(TableAttr.class);

                if (tableField.disable()) {
                    continue;
                }

                tableData.setWidth(tableField.width());
                tableData.setSort(tableField.sort());
                tableData.setSequence(tableField.sequence());
            }

            // 类型对象
            Class<?> type = field.getType();

            if (type.equals(List.class)) {
                ParameterizedType stringListType = (ParameterizedType) field.getGenericType();
                Class<?> listType = (Class<?>) stringListType.getActualTypeArguments()[0];

                // 资源属性
                if (Resource.class.isAssignableFrom(listType)) {
                    tableData.setType(FieldType.Resource);
                    tableData.setConfig(new ResourceConfig(getResourceType(listType), true));
                }

                // 实体属性
                else if (EntityKey.class.isAssignableFrom(listType)) {
                    String mappedBy = "";

                    if (field.isAnnotationPresent(OneToMany.class)) {
                        mappedBy = field.getAnnotation(OneToMany.class).mappedBy();
                    }

                    // 写入下级实体配置信息
                    tableData.setConfig(new SubclassConfig(mappedBy));
                    tableData.setType(FieldType.Subclass);
                }
            }

            // 资源属性
            else if (Resource.class.isAssignableFrom(type)) {
                tableData.setType(FieldType.Resource);
                tableData.setConfig(new ResourceConfig(getResourceType(type)));
            }

            else if (field.isAnnotationPresent(SelectField.class)) {
                tableData.setConfig(new SelectConfig());
            }

            table.add(tableData);
        }

        table.sort((arg0, arg1) -> (arg1.getSequence()).compareTo((arg0.getSequence())));

        return table;
    }

    /**
     * 获取资源属性类型
     * @param field 字段
     * @return 类型值
     */
    private static String getResourceType(Field field) {
        // 获取类型
        Class<?> type = field.getType();

        if (List.class.isAssignableFrom(type)) {
            return getResourceType(ListUtils.getGenericType(field));
        } else {
            return getResourceType(type);
        }
    }

    /**
     * 获取资源属性类型
     * @param type 类型 class
     * @return 类型值
     */
    private static String getResourceType(Class<?> type) {
        // 图片
        if (type.equals(ResourceImage.class)) {
            return "image";
        }

        // 视频
        if (type.equals(ResourceVideo.class)) {
            return "video";
        }

        // 音频
        if (type.equals(ResourceAudio.class)) {
            return "audio";
        }

        // 默认为文件
        return "file";
    }

    public static void setSelectValue(List<CreateData> create) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        for (CreateData data : create) {
            // 当字段为选择器时
            if (data.getType().equals(FieldType.Select)) {
                SelectConfig select = data.getSelectConfig();
                SelectField annotation = select.getSelectField();

                boolean isController = data.getSelectConfig().getController();

                List<Map<String, Object>> list = new ArrayList<>();

                if (isController) {
                    JpaController<?, ?> jpaController = ((Class<? extends EntityKey>) data.getSelectConfig().getType()).getConstructor().newInstance().getController();

                    if (StringUtils.isExist(annotation.filter())) {
                        for (EntityKey t : jpaController.controller.findCustomize(annotation.filter()).getData()) {
                            Map<String, Object> item = new HashMap<>();

                            item.put("id", t.getUuid());
                            item.put("value", EntityUtils.getTitleValue(t));

                            list.add(item);
                        }
                    } else {
                        for (EntityKey t : jpaController.controller.findAll().getData()) {
                            Map<String, Object> item = new HashMap<>();

                            item.put("id", t.getUuid());
                            item.put("value", EntityUtils.getTitleValue(t));

                            list.add(item);
                        }
                    }
                } else {
                    String[] v;

                    if (!annotation.variable().getName().equals(StringListValue.class.getName())) {
                        v = annotation.variable().getConstructor().newInstance().getContent();
                    } else {
                        v = annotation.fixed();
                    }

                    int i = 0;

                    // 是否为 int 类型字段
                    boolean isInt = Integer.class.isAssignableFrom(select.getType());

                    for (; i < v.length; i++) {
                        Map<String, Object> item = new HashMap<>();

                        String[] split = v[i].split(":");

                        if (split.length > 1) {
                            item.put("id", split[0]);
                            item.put("value", split[1]);
                        } else {
                            item.put("id", isInt ? i : split[0]);
                            item.put("value", split[0]);
                        }

                        list.add(item);
                    }
                }

                select.setData(list);
                select.setController(isController);
            }
        }
    }
}
