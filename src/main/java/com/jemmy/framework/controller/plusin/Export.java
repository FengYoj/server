package com.jemmy.framework.controller.plusin;

import com.jemmy.framework.annotation.FieldAttr;
import com.jemmy.framework.auto.page.annotation.field.TableAttr;
import com.jemmy.framework.component.resources.Resource;
import com.jemmy.framework.controller.Controller;
import com.jemmy.framework.controller.EntityKey;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Export {

    private final Class<? extends EntityKey> entity;

    private final Controller<?, ?> controller;

    public Export(Controller<?, ?> controller) {
        this.controller = controller;
        this.entity = controller.getEntity();
    }

    public HSSFWorkbook toExcel() {
        return toExcel(controller.findAll().getData());
    }

    public HSSFWorkbook toExcel(List<?> list) {

        List<Map<String, Object>> content;

        try {
            content = EntityUtils.processField(list);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HSSFWorkbook wb = new HSSFWorkbook();

        String name = EntityUtils.getEntityTitle(entity);

        HSSFSheet sheet = wb.createSheet(name);

        HSSFRow row;

        row = sheet.createRow(0);
        row.setHeight((short) (22.50 * 20));//设置行高

        List<Field> fields = EntityUtils.getFields(this.entity);

        row.createCell(0).setCellValue("序号");

        for (int i = 0; i < fields.size(); i++) {

            Field field = fields.get(i);

            if (isDisable(field)) {
                fields.remove(i);
                i--;
                continue;
            }

            FieldAttr fieldAttr = field.getDeclaredAnnotation(FieldAttr.class);

            String fieldName;

            if (fieldAttr != null && StringUtils.isExist(fieldAttr.value())) {
                fieldName = fieldAttr.value();
            } else {
                fieldName = field.getName();
            }

            row.createCell(i + 1).setCellValue(fieldName);
        }

        // 遍历所获取的数据
        for (int i = 0; i < content.size(); i++) {

            Map<String, Object> e = content.get(i);

            row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);

            for (int f_i = 0; f_i < fields.size(); f_i++) {
                Field field = fields.get(f_i);

                Object value = e.get(field.getName());

                HSSFCell cell = row.createCell(f_i + 1);

                // 当值为空时，写入横杠
                if (value == null) {
                    cell.setCellValue("-");

                    // 跳过
                    continue;
                }

                Class<?> clazz = value.getClass();

                if (Resource.class.isAssignableFrom(clazz)) {
                    value = ((Resource) value).getUrl();
                }

                cell.setCellValue(value.toString());
            }
        }

        sheet.setDefaultRowHeight((short) (16.5 * 20));

        // 列宽自适应
        for (int i = 0; i <= content.size(); i++) {
            sheet.autoSizeColumn(i);
        }

        return wb;
    }

    private Boolean isDisable(Field field) {
        TableAttr tableAttr = field.getDeclaredAnnotation(TableAttr.class);

        if (tableAttr == null) {
            return false;
        }

        return tableAttr.disable();
    }
}
