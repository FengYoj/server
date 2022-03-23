package com.jemmy.framework.component.weixin.qrcode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class QRCodeRequest {

    private String body;
    private Integer status;
    private HttpHeaders headers;
    private String message;

    /**
     * Http 请求
     *
     * @param url    域名
     * @param method 请求类型
     * @param params 请求参数
     */
    public QRCodeRequest(String url, HttpMethod method, String params) {
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        // 设置请求头
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> requestEntity = new HttpEntity<>(params, headers);
        // 执行 HTTP 请求
        try {
            ResponseEntity<String> response = client.exchange(url, method, requestEntity, String.class);
            body = response.getBody();
            status = response.getStatusCodeValue();
            this.headers = response.getHeaders();
        } catch (Exception e) {
            status = 400;
            message = "连接超时，服务器无响应！";
            Logger logger = LoggerFactory.getLogger(this.getClass());
            logger.error(e.getMessage());
        }
    }

    public String getBody() {
        return body;
    }

    public Integer getStatus() {
        return status;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getMessage() {
        return message;
    }
}
