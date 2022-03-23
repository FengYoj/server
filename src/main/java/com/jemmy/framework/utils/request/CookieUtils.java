package com.jemmy.framework.utils.request;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class CookieUtils {
    private final Map<String, String> map = new HashMap<>();

    public CookieUtils(HttpServletRequest request) {
        setCookie(request.getCookies());
    }

    public CookieUtils(Cookie[] cookies) {
        setCookie(cookies);
    }

    public CookieUtils(String cookies) {
        String[] cookieList = cookies.split(";\\s?");

        for (String cookie : cookieList) {
            String[] value = cookie.split("=");

            if (value.length == 2) {
                map.put(value[0], value[1]);
            }
        }
    }

    public String get(String key) {
        return map.get(key);
    }

    public String get(String key, String defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    public static String get(HttpServletRequest request, String key) {

        if (request == null) {
            return null;
        }

        return get(request.getCookies(), key);
    }

    public static String get(Cookie[] cookies, String key) {
        if (cookies != null) {
            for(Cookie cookie : cookies){
                if (cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setCookie(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
        }
    }
}
