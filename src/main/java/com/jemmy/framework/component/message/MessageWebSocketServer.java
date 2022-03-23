package com.jemmy.framework.component.message;

import com.jemmy.framework.component.websocket.WebSocket;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint(value = "/WebSocket/Message")
public class MessageWebSocketServer extends WebSocket {

}
