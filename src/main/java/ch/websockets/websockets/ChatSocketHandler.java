package ch.websockets.websockets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.gson.Gson;

public class ChatSocketHandler extends TextWebSocketHandler {
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws InterruptedException, IOException {
        Map<?, ?> value = new Gson().fromJson(message.getPayload(), Map.class);
        System.out.println("Received from " + session.getId() + ": " + value);

        if (value.containsKey("sender") && value.containsKey("text")) {
            // Broadcast the message to all clients
            for (WebSocketSession webSocketSession : sessions) {
                if (webSocketSession.isOpen()) {
                    webSocketSession.sendMessage(new TextMessage(new Gson().toJson(value)));
                }
            }
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        System.out.println("Connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("Disconnected: " + session.getId());
    }
}
