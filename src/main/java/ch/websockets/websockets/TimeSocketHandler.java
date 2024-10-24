package ch.websockets.websockets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class TimeSocketHandler extends TextWebSocketHandler {
    List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

    public TimeSocketHandler() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                    List<WebSocketSession> disconnectedSessions = new ArrayList<>();
                    for (WebSocketSession session : sessions) {
                        try {
                            if (session.isOpen()) {
                                session.sendMessage(new TextMessage(time));
                            } else {
                                disconnectedSessions.remove(session);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    sessions.removeAll(disconnectedSessions);
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        thread.start();
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

