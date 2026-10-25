package keenay.education.config;

import keenay.education.exception.WebSocketErrorHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer  {

    private final JwtChannelInterceptor jwtChannelInterceptor;
    private final WebSocketErrorHandler webSocketErrorHandler;

    @Value("${rabbitmq.stomp.host}")
    private String stompHost;
    @Value("${rabbitmq.stomp.port}")
    private int stompPort;
    @Value("${rabbitmq.stomp.login}")
    private String stompLogin;
    @Value("${rabbitmq.stomp.passcode}")
    private String stompPasscode;


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");

        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(stompHost)
                .setRelayPort(stompPort)
                .setClientLogin(stompLogin)
                .setClientPasscode(stompPasscode)
                .setSystemLogin(stompLogin)
                .setSystemPasscode(stompPasscode);
    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(webSocketErrorHandler);
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtChannelInterceptor);
    }
}