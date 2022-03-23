package com.jemmy.framework.utils;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    public static List<Map<String, String>> toList(InputStream inputStream) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);

        //定义工作表
        XSSFSheet xssfSheet = workbook.getSheetAt(0);

        List<Map<String, String>> list = new ArrayList<Map<String, String>>();

        //定义行
        //默认第一行为标题行，index = 0
        XSSFRow titleRow = xssfSheet.getRow(0);

        //循环取每行的数据
        for (int rowIndex = 1; rowIndex < xssfSheet.getPhysicalNumberOfRows(); rowIndex++) {
            XSSFRow xssfRow = xssfSheet.getRow(rowIndex);
            if (xssfRow == null) {
                continue;
            }

            Map<String, String> map = new LinkedHashMap<>();

            // 循环取每个单元格(cell)的数据
            for (int i = 0; i < xssfRow.getLastCellNum(); i++) {
                XSSFCell titleCell = titleRow.getCell(i);
                XSSFCell xssfCell = xssfRow.getCell(i);

                String value = getString(xssfCell);

                if (StringUtils.isExist(value)) {
                    map.put(getString(titleCell) , value);
                }
            }

            list.add(map);
        }

        return list;
    }

    private static String getString(XSSFCell xssfCell) {
        if (xssfCell == null) {
            return "";
        }

        xssfCell.setCellType(CellType.STRING);
        return xssfCell.getStringCellValue();
    }
}
