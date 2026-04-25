package com.itcast.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/ws/message")
@Component
@Slf4j
public class WebSocketServer {

    private static CopyOnWriteArraySet<WebSocketServer> webSockets = new CopyOnWriteArraySet<>();
    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSockets.add(this);
        log.info("WebSocket连接建立，当前连接数: {}", webSockets.size());
    }

    @OnClose
    public void onClose() {
        webSockets.remove(this);
        log.info("WebSocket连接关闭，当前连接数: {}", webSockets.size());
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("收到WebSocket消息: {}", message);
        sendMessageToAll("服务端收到: " + message);
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    public static void sendMessageToAll(String message) {
        for (WebSocketServer webSocket : webSockets) {
            webSocket.sendMessage(message);
        }
    }
}
