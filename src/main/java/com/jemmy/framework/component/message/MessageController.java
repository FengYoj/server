package com.jemmy.framework.component.message;

import com.jemmy.config.RequestPath;
import com.jemmy.framework.auto.admin.Admin;
import com.jemmy.framework.auto.api.annotation.AutoAPI;
import com.jemmy.framework.auto.api.annotation.Get;
import com.jemmy.framework.auto.api.annotation.Post;
import com.jemmy.framework.auto.param.AutoParam;
import com.jemmy.framework.component.json.JemmyJson;
import com.jemmy.framework.controller.JpaController;
import com.jemmy.framework.utils.EntityUtils;
import com.jemmy.framework.utils.result.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AutoAPI
public class MessageController extends JpaController<Message, MessageRepository> {

    private final MessageWebSocketServer webSocketServer;

    public MessageController(MessageWebSocketServer webSocketServer) {
        this.webSocketServer = webSocketServer;
    }

    @AutoAPI
    public class _ADMIN extends Admin<Message, MessageController> {

        @Override
        public Result<?> findProcessEntity(String uuid) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
            Result<Message> result = controller.findByUuid(uuid);

            if (result.isBlank()) {
                return result;
            }

            Message message = result.getData();

            if (message.getReadStatus().equals(0)) {
                // Set read status to 1
                message.setReadStatus(1);
                // Save
                controller.onlySave(message);
            }

            return Result.HTTP200().setData(EntityUtils.processField(message));
        }
    }

    public Result<String> save(Message entity) {
        Result<String> result = controller.save(entity);

        if (result.isNormal()) {
            // 发送 Socket 到前端
            webSocketServer.send(entity);
        }

        return result;
    }

    @Get(path = RequestPath.ADMIN)
    public Result<List<Message>> findAllUnread() {
        return Result.<List<Message>>HTTP200().setData(repository.findAllByUnread());
    }

    @Get(path = RequestPath.ADMIN)
    public Result<Page<Message>> findAllUnreadToPage(@AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer size) {
        return Result.<Page<Message>>HTTP200().setData(repository.findAllByUnread(PageRequest.of(page, size, Sort.Direction.DESC, "createdDate")));
    }

    @Get(path = RequestPath.ADMIN)
    public Result<Page<Message>> findAllBySourceToPage(@AutoParam Integer page, @AutoParam(required = false, defaults = "10") Integer size, @AutoParam String source) {
        return Result.<Page<Message>>HTTP200().setData(repository.findAllBySource(MessageSource.get(source), PageRequest.of(page, size, Sort.Direction.DESC, "createdDate")));
    }

    @Get(path = RequestPath.ADMIN)
    public Result<Page<Message>> findAllToPage(@AutoParam Integer page, @AutoParam Integer size) {
        return controller.findAllToPage(page, size);
    }

    @Post(path = RequestPath.ADMIN)
    public Result<?> markReadStatus(@AutoParam Message message, @AutoParam(verify = { "0", "1" }) Integer status) {
        message.setReadStatus(status);
        // 保存
        return save(message).toEmpty();
    }

    @Post(path = RequestPath.ADMIN)
    public Result<?> markReadStatusAll(@AutoParam(verify = { "0", "1" }) Integer status) {
        repository.updateAllReadStatus(status);
        // 保存
        return Result.HTTP200();
    }

    @Get(path = RequestPath.ADMIN)
    public Result<Map<String, Object>> findInfo() {
        Map<String, Object> res = new HashMap<>();

        res.put("unread", repository.findUnreadCount());
        res.put("all", repository.findAllCount());
        res.put("menu", this.getAllSource());

        return Result.<Map<String, Object>>HTTP200().setData(res);
    }

    private List<JemmyJson> getAllSource() {
        List<JemmyJson> res = new ArrayList<>();

        List<Map<String, Object>> list = repository.findAllSource();

        for (Map<String, Object> map : list) {
            JemmyJson json = new JemmyJson();

            json.put("source", map.get("source"));
            json.put("count", map.get("count"));

            res.add(json);
        }

        return res;
    }
}
