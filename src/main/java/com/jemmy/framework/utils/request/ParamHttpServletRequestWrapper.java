package com.jemmy.framework.utils.request;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.utils.StringUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Pattern;

public class ParamHttpServletRequestWrapper extends HttpServletRequestWrapper {
    // 用于将流保存下来
    private final byte[] requestBody;

    private final JemmyJson params = new JemmyJson();

    public ParamHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        this.setParams(request);

        requestBody = StreamUtils.copyToByteArray(request.getInputStream());

        String type = request.getContentType();

        // 遇到 类型 为 json 才缓存所有 json 参数
        if (StringUtils.isExist(type) && Pattern.matches(".*application/json.*", type)) {
            params.put(RequestParam.getInputStreamParams(this.getInputStream()));
        }
    }

    private void setParams(HttpServletRequest request) {
        Map<String, String[]> paramMap = request.getParameterMap();

        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            String[] value = param.getValue();

            if (value != null && value.length > 0) {
                params.put(param.getKey(), value[0]);
            }
        }
    }

    public JemmyJson getParams() {
        return params;
    }

    @Override
    public ServletInputStream getInputStream() {

        final ByteArrayInputStream stream = new ByteArrayInputStream(requestBody);

        return new ServletInputStream() {

            @Override
            public int read() {
                return stream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

}
