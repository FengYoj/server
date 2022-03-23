package com.jemmy.framework.utils.request;

import com.jemmy.framework.component.json.JemmyForm;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.config.TrustAllTrustManager;
import com.jemmy.framework.utils.StringUtils;
import io.undertow.server.handlers.form.FormData;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSessionContext;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Request {

    private String statusName;

    private Object statusValue;

    private String msgName;

    private String dataName;

    public Request(String statusName, Object statusValue) {
        this.statusName = statusName;
        this.statusValue = statusValue;
    }

    public Request(String statusName, Object statusValue, String msgName) {
        this.statusName = statusName;
        this.statusValue = statusValue;
        this.msgName = msgName;
    }

    public Request(String statusName, Object statusValue, String msgName, String dataName) {
        this.statusName = statusName;
        this.statusValue = statusValue;
        this.msgName = msgName;
        this.dataName = dataName;
    }

    public Request() {
    }

    public RequestResult send(HttpRequest request) {
        return send(request, statusName, statusValue, msgName, dataName);
    }

    public static RequestResult send(HttpRequest request, String status_name, Object status_val, String msg_name, String dataName) {
        try {
            HttpResponse<String> res = getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println(res.body());

            if (res.statusCode() != 200) {
                return new RequestResult(res.statusCode());
            }

            JemmyJson json = JemmyJson.toJemmyJson(res.body());

            if (StringUtils.isExist(status_name) && json.containsKey(status_name)) {
                if (!json.get(status_name).equals(status_val)) {

                    if (StringUtils.isExist(msg_name)) {
                        return new RequestResult(json.get(status_name), json.getString(msg_name));
                    }

                    return new RequestResult(json.get(status_name));
                }
            }

            String data;

            if (StringUtils.isExist(dataName)) {
                data = JemmyJson.toJSONString(json.get(dataName));
            } else {
                data = res.body();
            }

            return new RequestResult(200, "Success", data);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return new RequestResult(500, e.getMessage());
        }
    }

    public RequestResult post(URI uri) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        return send(httpRequest);
    }

    public RequestResult post(URI uri, JemmyForm form) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form.toFormDataString()))
                .build();

        return send(httpRequest);
    }

    public RequestResult post(String url, Object param) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(JemmyJson.toJSONString(param)))
                .setHeader("Accept", "application/json")
                .setHeader("Content-Type", "application/json")
                .build();

        return send(httpRequest);
    }

    public RequestResult get(URI uri) {
        var httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        return send(httpRequest);
    }

    public static HttpClient getHttpClient() {
        try {
            //  配置认证管理器
            javax.net.ssl.TrustManager[] trustAllCerts = {new TrustAllTrustManager()};
            SSLContext sc = SSLContext.getInstance("SSL");
            SSLSessionContext sslsc = sc.getServerSessionContext();
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);
            HttpClient.Builder builder = HttpClient.newBuilder();
            builder.sslContext(sc);
            return builder.build();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }
}
