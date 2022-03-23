package com.jemmy.framework.component.weixin.qrcode;

import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.weixin.WxUrl;
import com.jemmy.framework.component.weixin.access_token.AccessTokenInt;
import com.jemmy.framework.utils.request.Uri;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("WebAPI/WeiXin/QRCode")
public class QRCodeController {

    public QRCodeController() {
    }

    @GetMapping("Get")
    public Object get(@RequestParam String param, @RequestParam(required = false) String page, HttpServletResponse response) throws IOException, InterruptedException {
        response.setHeader("Content-Type", "image/jpeg");

        BufferedImage bi1 = this.get(param, page);

        ImageIO.write(bi1, "png", response.getOutputStream());

        return "success";
    }

    public BufferedImage get(String param, String page) throws IOException, InterruptedException {
        var client = HttpClient.newHttpClient();

        Map<String, String> map = new HashMap<>();

        map.put("scene", param);
        map.put("page", page);

        var httpRequest = HttpRequest.newBuilder()
                .uri(Uri.of(WxUrl.wxacodeunlimit).setParam("access_token", AccessTokenInt.WE.get())
                        .build())
                .POST(HttpRequest.BodyPublishers.ofString(JemmyJson.toJSONString(map)))
                .build();

        HttpResponse<byte[]> res = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

        ByteArrayInputStream stream = new ByteArrayInputStream(res.body());

        return ImageIO.read(stream);
    }

    public Image getImage(String param, String page) throws IOException, InterruptedException {
        var bi = this.get(param, page);

        return new ImageIcon(bi).getImage();
    }

}
