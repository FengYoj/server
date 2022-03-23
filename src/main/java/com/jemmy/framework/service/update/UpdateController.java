package com.jemmy.framework.service.update;

import com.jemmy.config.PathConfig;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.component.websocket.WebSocket;
import com.jemmy.framework.config.ServiceSupport;
import com.jemmy.framework.utils.file.FileInfo;
import com.jemmy.framework.utils.file.FileUtils;
import com.jemmy.framework.utils.request.Request;
import com.jemmy.framework.utils.request.RequestResult;
import com.jemmy.framework.utils.request.Uri;
import com.jemmy.framework.utils.result.Result;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.File;

@AutoAPI("Update")
@Component
@ServerEndpoint(value = "/WebSocket/Update")
public class UpdateController extends WebSocket {

    private final Request request = new Request();

    @Get(path = RequestPath.ADMIN)
    public Result<JemmyJson> check(@AutoParam String version) {

        Uri uri = new Uri(ServiceSupport.domain + "/WebAPI/Program/Check")
                .setParam("version", version)
                .setParam("platform", "server_admin");

        return request.get(uri.build()).toJsonStatus();
    }

    private void update(String version, Session session) {
        Uri uri = new Uri(ServiceSupport.domain + "/WebAPI/Program/FindLatestByVersion")
                .setParam("version", version)
                .setParam("platform", "server_admin");

        RequestResult status = request.get(uri.build());

        if (status.isBlank()) {
            send(session, new com.jemmy.framework.service.update.Result("Error", "版本获取失败"));

            // 终止
            return;
        }

        JemmyJson json = status.toJson();

        FileInfo fileInfo;

        try {
            fileInfo = FileUtils.download(json.getJemmyJson("file").getString("url"));
        } catch (Exception e) {
            send(session, new com.jemmy.framework.service.update.Result("Error", "下载版本失败！"));

            // 打印控制台
            e.printStackTrace();

            // 终止
            return;
        }

        // 压缩包文件
        File file = fileInfo.getFile();

        // 源文件
        File source = new File(PathConfig.admin);

        // 备份文件路径
        File backup = new File(PathConfig.admin.replaceAll("/*$", "") + "_backup");

        // 源文件拷贝至备份路径
        FileUtils.copyDirectory(source, backup);

        // 删除源文件
        FileUtils.delete(source);

        // 解压缩
        if (FileUtils.unZip(file.getPath(), PathConfig.admin)) {
            send(session, new com.jemmy.framework.service.update.Result("SuccessUpdate"));

            // 删除备份文件
            FileUtils.delete(backup);
        } else {
            send(session, new com.jemmy.framework.service.update.Result("Error", "升级失败，请稍后再试！"));

            // 恢复备份文件
            FileUtils.copyDirectory(backup, source);
        }
    }

    private Boolean updating = false;

    @Override
    public void onMessage(String message, Session session) {
        try {
            JemmyJson json = JemmyJson.toJemmyJson(message);

            if (!json.containsKey("version")) {
                return;
            }

            String method = json.getString("method");

            switch (method) {
                case "Update":
                    send(new com.jemmy.framework.service.update.Result("Info", "正在升级中，请稍后！"));

                    if (!updating) {
                        updating = true;

                        this.update(json.getString("version"), session);

                        updating = false;
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
