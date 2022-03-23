package com.jemmy.framework.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static boolean isExist(String str) {
        return !isBlank(str);
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static String defaults(String str, String defaults) {
        if (isBlank(str)) {
            return defaults;
        }

        return str;
    }

    public static String upperCase(String str) {
        char[] ch = str.toCharArray();

        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }

        return new String(ch);
    }

    public static String toUnderline(String camelCase){
        char[] charArray = camelCase.toCharArray();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0,l=charArray.length; i < l; i++) {
            if (charArray[i] >= 65 && charArray[i] <= 90) {
                if (buffer.length() > 0) {
                    buffer.append("_");
                }
                buffer.append(charArray[i] += 32);
            }else {
                buffer.append(charArray[i]);
            }
        }
        return buffer.toString();
    }

    public static String getMatcher(String regex, String source) {
        String result = "";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(source);
        while (matcher.find()) {
            result = matcher.group(1);
        }
        return result;
    }
}
