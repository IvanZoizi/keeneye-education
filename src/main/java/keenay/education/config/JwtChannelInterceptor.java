package keenay.education.config;

import io.jsonwebtoken.JwtException;
import keenay.education.security.jwt.JwtService;
import keenay.education.service.impl.CustomUserServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final CustomUserServiceImpl customUserService;
    private final WsSessionRegistry sessionRegistry;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || accessor.getCommand() == null) {
            return message;
        }

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            handleConnect(accessor);
            return message;
        }

        if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
            if (accessor.getUser() == null) {
                throw new AuthenticationCredentialsNotFoundException("Unauthorized");
            }
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        if (authHeader == null || authHeader.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Missing Authorization header on CONNECT");
        }

        Authentication auth;
        try {
            String token = jwtService.getJwtToken(authHeader);

            UserDetails userDetails = customUserService.getUserByEmail(token);
            auth = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
        } catch (IllegalArgumentException | JwtException ex) {
            log.warn("WS auth failed: {}", ex.getMessage());
            throw new AuthenticationCredentialsNotFoundException("Invalid or expired token");
        }

        if (!sessionRegistry.tryRegister(auth.getName(), accessor.getSessionId())) {
            log.warn("WS connection rejected, user already connected: {}", auth.getName());
            throw new MessageDeliveryException("Already connected from another session");
        }

        accessor.setUser(auth);
    }
}