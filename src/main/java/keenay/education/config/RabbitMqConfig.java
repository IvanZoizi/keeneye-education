package keenay.education.config;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RabbitMqConfig {

    public static final String EMAIL_EXCHANGE = "email.exchange";

    private final String nameQueue;
    private final String nameQueueRetry;
    private final String nameQueueException;
    private final Integer timeRetry;

    public RabbitMqConfig(
            @Value("${rabbitmq.queue.name}") String nameQueue,
            @Value("${rabbitmq.queue.name-retry}") String nameQueueRetry,
            @Value("${rabbitmq.queue.name-exception}") String nameQueueException,
            @Value("${rabbitmq.queue.time-retry}") Integer timeRetry
    ) {
        this.nameQueue = nameQueue;
        this.nameQueueRetry = nameQueueRetry;
        this.nameQueueException = nameQueueException;
        this.timeRetry = timeRetry;
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(nameQueue, true);
    }

    @Bean
    public Queue emailRetryQueue() {
        return QueueBuilder.durable(nameQueueRetry)
                .withArgument("x-message-ttl", timeRetry)
                .build();
    }

    @Bean
    public Queue emailExceptionQueue() {
        return new Queue(nameQueueException, true);
    }

    @Bean
    public Binding sendBindingQueue(Queue emailQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailQueue).to(emailExchange).with(nameQueue);
    }

    @Bean
    public Binding retryBindingQueue(Queue emailRetryQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailRetryQueue).to(emailExchange).with(nameQueueRetry);
    }

    @Bean
    public Binding exceptionBindingQueue(Queue emailExceptionQueue, TopicExchange emailExchange) {
        return BindingBuilder.bind(emailExceptionQueue).to(emailExchange).with(nameQueueException);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
