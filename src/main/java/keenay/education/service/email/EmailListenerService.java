package keenay.education.service.email;

import keenay.education.config.RabbitMqConfig;
import keenay.education.dto.email.EmailDTO;
import keenay.education.entity.EmailsUser;
import keenay.education.exception.ExceptionDetection;
import keenay.education.exception.errors.NoMailFoundException;
import keenay.education.repository.EmailsUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class EmailListenerService {

    private final EmailSender emailSender;
    private final ExceptionDetection exceptionDetection;
    private final RabbitMqConfig rabbitMqConfig;
    private final RabbitTemplate rabbitTemplate;
    private final EmailsUserRepository emailsUserRepository;
    private final String nameQueueRetry;
    private final String nameQueueException;

    public EmailListenerService(
            EmailSender emailSender,
            ExceptionDetection exceptionDetection,
            RabbitMqConfig rabbitMqConfig,
            RabbitTemplate rabbitTemplate,
            EmailsUserRepository emailsUserRepository,
            @Value("${rabbitmq.queue.name-retry}") String nameQueueRetry,
            @Value("${rabbitmq.queue.name-exception}") String nameQueueException
    ) {
        this.emailSender = emailSender;
        this.exceptionDetection = exceptionDetection;
        this.rabbitMqConfig = rabbitMqConfig;
        this.rabbitTemplate = rabbitTemplate;
        this.emailsUserRepository = emailsUserRepository;
        this.nameQueueRetry = nameQueueRetry;
        this.nameQueueException = nameQueueException;
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void sendEmail(EmailDTO emailDTO) {
        try {
            emailSender.sendSimpleEmail(emailDTO.getEmail(), emailDTO.getSubject(), emailDTO.getText());
            EmailsUser emailsUser = emailsUserRepository.findById(emailDTO.getEmailsUserId())
                    .orElseThrow(() -> new NoMailFoundException("This email has not been found."));
            emailsUser.setExceptionMessage(null);
            emailsUser.setIsSended(true);
            emailsUserRepository.save(emailsUser);
        } catch (Throwable exception) {
            if (exceptionDetection.checkRetentionException(exception)) {
                if (emailDTO.getAttempt() > 0) {
                    emailDTO.setAttempt(emailDTO.getAttempt() - 1);
                    rabbitTemplate.convertAndSend(rabbitMqConfig.EMAIL_EXCHANGE, nameQueueRetry, emailDTO);
                } else {
                    emailDTO.setEx(exception);
                    emailDTO.setIsSend(false);
                    emailDTO.setAttempt(0);
                    rabbitTemplate.convertAndSend(rabbitMqConfig.EMAIL_EXCHANGE, nameQueueException, emailDTO);
                }
            } else {
                emailDTO.setIsSend(false);
                emailDTO.setEx(exception);
                rabbitTemplate.convertAndSend(rabbitMqConfig.EMAIL_EXCHANGE, nameQueueException, emailDTO);
            }
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.name-retry}")
    public void sendEmailRetry(EmailDTO emailDTO) {
        EmailsUser emailsUser = emailsUserRepository.findById(emailDTO.getEmailsUserId())
                .orElseThrow(() -> new NoMailFoundException("This email has not been found."));
        emailsUserRepository.incrementCountRetry(emailsUser.getId(), emailsUser.getExceptionMessage());
        sendEmail(emailDTO);
    }

    @RabbitListener(queues = "${rabbitmq.queue.name-exception}")
    public void emailWithException(EmailDTO emailDTO) {
        EmailsUser emailsUser = emailsUserRepository.findById(emailDTO.getEmailsUserId())
                .orElseThrow(() -> new NoMailFoundException("This email has not been found."));
        emailsUser.setExceptionMessage(emailDTO.getEx().getMessage());
        emailsUserRepository.save(emailsUser);
    }
}