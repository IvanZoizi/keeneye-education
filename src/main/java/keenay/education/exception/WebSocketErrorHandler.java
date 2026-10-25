package keenay.education.exception;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
public class WebSocketErrorHandler extends StompSubProtocolErrorHandler {

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage, Throwable ex) {

        Throwable cause = (ex instanceof MessageDeliveryException && ex.getCause() != null)
                ? ex.getCause() : ex;

        String msg = cause.getMessage() != null ? cause.getMessage() : "WebSocket error";
        return buildError(msg);
    }

    private Message<byte[]> buildError(String message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setMessage(message);
        accessor.setLeaveMutable(true);
        return MessageBuilder.createMessage(
                message.getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
}