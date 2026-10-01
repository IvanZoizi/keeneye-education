package keenay.education.service.email;

import keenay.education.config.RabbitMqConfig;
import keenay.education.dto.email.EmailDTO;
import keenay.education.entity.EmailsUser;
import keenay.education.entity.Users;
import keenay.education.mapper.email.EmailMapper;
import keenay.education.repository.jpa.EmailsUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailCreateApplicationService {
    private final RabbitMqConfig rabbitMqConfig;
    private final RabbitTemplate rabbitTemplate;
    private final EmailsUserRepository emailsUserRepository;
    private final EmailMapper emailMapper;

    @Value("${rabbitmq.queue.name}")
    private String nameQueue;

    @Value("${rabbitmq.queue.retry-attempt}")
    private Integer retryAttempt;

    private EmailsUser createEmailsUsers(Users user, String text, String subject) {
        EmailsUser emailsUser = emailMapper.getEmailsUser(user.getId(), text, subject, retryAttempt);
        return emailsUserRepository.save(emailsUser);
    }

    public void sendEmailFor(Users user, String text, String subject) {
        EmailsUser emailsUser = createEmailsUsers(user, text, subject);
        EmailDTO emailDTO = emailMapper.getDTO(user.getId(), user.getEmail(), text, subject, retryAttempt, emailsUser.getId());
        rabbitTemplate.convertAndSend(rabbitMqConfig.EMAIL_EXCHANGE, nameQueue, emailDTO);
    }
}
