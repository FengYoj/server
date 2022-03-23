package com.jemmy.framework.utils.request;

import com.jemmy.framework.component.json.JemmyJson;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;

public class RequestParam {

    private final HttpServletRequest request;

    public RequestParam() {
        request = RequestUtils.getServlet();
    }

    public RequestParam(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * 获取所有请求头参数
     * @return JemmyJson
     */
    public JemmyJson getHeader() {
        Enumeration<String> names = request.getHeaderNames();

        JemmyJson res = new JemmyJson();

        while(names.hasMoreElements()){
            String name = names.nextElement();
            res.put(name, request.getHeader(name));
        }

        return res;
    }

    /**
     * 获取请求头参数
     * @param key key
     * @return String
     */
    public String getHeader(String key) {
        return request.getHeader(key);
    }

    /**
     * 获取所有 Cookie 参数
     * @return JemmyJson
     */
    public JemmyJson getCookie() {
        Cookie[] cookies = request.getCookies();

        JemmyJson res = new JemmyJson();

        for (Cookie cookie : cookies) {
            res.put(cookie.getName(), cookie.getValue());
        }

        return res;
    }

    /**
     * 获取 Cookie 参数
     * @return String
     */
    public String getCookie(String key) {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    /**
     * 获取所有参数
     * @return JemmyJson
     */
    public JemmyJson getParam() {
        if (request instanceof ParamHttpServletRequestWrapper && ((ParamHttpServletRequestWrapper) request).getParams() != null) {
            return ((ParamHttpServletRequestWrapper) request).getParams();
        }

        JemmyJson res = getInputStreamParams(request);

        if (res == null) {
            res = new JemmyJson();
        }

        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            if (param.getValue().length > 0) {
                res.put(param.getKey(), param.getValue()[0]);
            }
        }

        return res;
    }

    /**
     * 获取 参数 and 指定类型
     * @return T
     */
    public <T> T getParam(String key, Class<T> type) {
        JemmyJson res = getInputStreamParams(request);

        if (res != null && res.containsKey(key)) {
            return res.get(key, type);
        }

        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {

            if (param.getKey().equals(key)) {
                return JemmyJson.toJavaObject(param.getValue()[0], type);
            }
        }

        return null;
    }

    /**
     * 获取参数
     * @return T
     */
    public <T> T getParam(String key) {
        JemmyJson res = getInputStreamParams(request);

        if (res != null && res.containsKey(key)) {
            return res.get(key);
        }

        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {

            if (param.getKey().equals(key)) {
                return JemmyJson.toJavaObject(param.getValue()[0]);
            }
        }

        return null;
    }

    public static JemmyJson getInputStreamParams(HttpServletRequest request) {
        try {
            return getInputStreamParams(request.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return new JemmyJson();
        }
    }

    public static JemmyJson getInputStreamParams(ServletInputStream inputStream) {
        JemmyJson res;

        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            char[] charBuffer = new char[128];
            int bytesRead;
            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                stringBuilder.append(charBuffer, 0, bytesRead);
            }

            if (stringBuilder.length() > 0) {
                res = JemmyJson.toJemmyJson(stringBuilder.toString());
            } else {
                res = new JemmyJson();
            }
        } catch (Exception e) {
            res = new JemmyJson();
        }

        return res;
    }

    public String getXmlParams() {
        StringBuilder sb = new StringBuilder();
        String inputLine = null;

        try (BufferedReader bufferedReader = request.getReader()) {
            while (true) {
                try {
                    if ((inputLine = bufferedReader.readLine()) == null) break;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sb.append(inputLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
