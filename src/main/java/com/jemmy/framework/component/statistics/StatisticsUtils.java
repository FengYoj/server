package com.jemmy.framework.component.statistics;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsUtils {

    public static List<Long> daysToLongs(List<Object[]> list, Integer day) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return toLongs(list, day, Calendar.DATE, sdf);
    }

    public static List<Long> monthsToLongs(List<Object[]> list, Integer month) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        return toLongs(list, month, Calendar.MONTH, sdf);
    }

    private static List<Long> toLongs(List<Object[]> list, Integer size, Integer amount, SimpleDateFormat sdf) {
        List<Long> dates = new ArrayList<>();
        Calendar c = Calendar.getInstance();

        for (int i = 0 ; i < size; i++, c.add(amount, -1)) {
            String date = sdf.format(c.getTime());

            long q = 0L;

            for (Object[] obj : list) {
                if (obj[0].equals(date)) {
                    Object value = obj[1];

                    if (value instanceof BigInteger) {
                        q = (((BigInteger) value).longValue());
                    } else if (value instanceof BigDecimal) {
                        q = ((BigDecimal) value).longValue();
                    } else if (value != null) {
                        q = (Long) value;
                    }
                }
            }

            // 从第 0 位添加
            dates.add(0, q);
        }

        return dates;
    }
}
