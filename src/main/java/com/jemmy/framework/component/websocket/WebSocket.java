package com.jemmy.framework.component.websocket;

import com.jemmy.framework.component.json.JemmyJson;
import lombok.SneakyThrows;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@NoRepositoryBean
public class WebSocket implements ApplicationListener<ContextClosedEvent> {

    private static int onlineCount = 0;
    // concurrent 包的线程安全Set，用来存放每个客户端对应的Session对象。
    protected static final CopyOnWriteArraySet<WebSocket> sessionSet = new CopyOnWriteArraySet<>();

    // key
    private String key;
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String keyName;

    public WebSocket() {
    }

    public WebSocket(String keyName) {
        this.keyName = keyName;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void _onOpen(Session session) {
        if (keyName != null) {
            Map<String, List<String>> map = session.getRequestParameterMap();

            if (!map.containsKey(keyName)) {
                return;
            }

            this.key = map.get(keyName).get(0);
        }

        this.session = session;
        sessionSet.add(this);
        onlineCount++;
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void _onClose(Session session) {
        for (WebSocket socket : sessionSet) {
            if (socket.session.equals(session)) {
                sessionSet.remove(socket);
                break;
            }
        }
    }

    @OnMessage
    public void _onMessage(String message, Session session) {
        this.onMessage(message, session);
    }

    public void onMessage(String message, Session session) {
        JemmyJson json = JemmyJson.toJemmyJson(message);

        if (json.containsKey("key")) {
            this.send(json.getString("key"), json);
        }
    }

    /**
     * 发送消息
     */
    public void send(Object value) {
        for (WebSocket socket : sessionSet) {
            if (socket.session.isOpen()) {
                try {
                    socket.session.getBasicRemote().sendText(JemmyJson.toJSONString(value));
                } catch (IOException err) {
                    err.printStackTrace();
                }
            }
        }
    }

    public void send(Session session, Object value) {
        try {
            session.getBasicRemote().sendText(JemmyJson.toJSONString(value));
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    public void send(String key, Object value) {
        try {
            for (WebSocket item : sessionSet) {
                if(item.key.equals(key)){
                    item.session.getBasicRemote().sendText(JemmyJson.toJSONString(value));
                }
            }
        } catch (IOException err) {
            err.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void onApplicationEvent(@NonNull ContextClosedEvent event) {
        for (WebSocket socket : sessionSet) {
            if (socket.session.isOpen()) {
                socket.session.close();
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }
}
