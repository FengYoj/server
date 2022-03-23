package com.jemmy.framework.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class DateUtils {

    public static Boolean isExpired(String dateStr) {
        try {
            boolean isDate = Pattern.matches("^\\d*-\\d*-\\d*$", dateStr);
            String dateEnd = isDate ? dateStr : dateStr.replace("T", " ");
            DateFormat df = new SimpleDateFormat(isDate ? "yyyy-MM-dd" : "yyyy-MM-dd HH:mm");
            Date dt = df.parse(dateEnd);
            if ((dt.getTime() + (isDate ? (1000 * 60 * 60 * 24) : 0)) < new Date().getTime()) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * 判断延时过期
     *
     * @param date 日期
     * @param delay   延时毫秒数
     * @return 是否过期
     */
    public static Boolean isDelayExpired(Date date, Long delay) {
        return (date.getTime() + delay) < new Date().getTime();
    }
}
