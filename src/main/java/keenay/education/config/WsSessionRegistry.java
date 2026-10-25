package keenay.education.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class WsSessionRegistry {

    private final ConcurrentMap<String, String> sessions = new ConcurrentHashMap<>();

    public boolean tryRegister(String username, String sessionId) {
        return sessions.putIfAbsent(username, sessionId) == null;
    }

    public void release(String username, String sessionId) {
        sessions.remove(username, sessionId);
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(event.getMessage());
        Principal user = acc.getUser();
        if (user != null) this.release(user.getName(), acc.getSessionId());
    }

}