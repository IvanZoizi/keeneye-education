package keenay.education.service.impl;

import keenay.education.dto.chat.ChatMessageBodyDTO;
import keenay.education.entity.Chat;
import keenay.education.entity.Messages;
import keenay.education.entity.Users;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.exception.errors.ChatNotFoundException;
import keenay.education.exception.errors.UserIsNotFoundException;
import keenay.education.mapper.chat.ChatMapper;
import keenay.education.repository.ChatRepository;
import keenay.education.repository.MessagesRepository;
import keenay.education.repository.RolesRepository;
import keenay.education.repository.UserRepository;
import keenay.education.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final MessagesRepository messagesRepository;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final ChatMapper chatMapper;

    private Messages create(Chat chat, ChatMessageBodyDTO chatMessageBodyDTO, Users user) {
        Messages message = Messages.builder()
                .chat(chat)
                .text(chatMessageBodyDTO.getText())
                .sender(user)
                .build();
        if (chat.getCustomer().getId().equals(user.getId())) {
            message.setRole(rolesRepository.findByRole("customer")
                    .orElseThrow());
        } else if (chat.getSeller().getId().equals(user.getId())) {
            message.setRole(rolesRepository.findByRole("seller")
                    .orElseThrow());
        }  else {
            throw new AccessDeniedException("User is not a participant of this chat");
        }
        return messagesRepository.save(message);
    }

    @Override
    public void newMessage(Long chatId, ChatMessageBodyDTO chatMessageBodyDTO, Principal principal)  {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat is not found."));
        Users user = userRepository.findByEmail(principal.getName()).
                orElseThrow(() -> new UserIsNotFoundException("User is not found"));
        Messages message = create(chat, chatMessageBodyDTO, user);
        messagingTemplate.convertAndSend(
                "/queue/chats/" + chatId,
                chatMapper.getDTO(message)
        );
    }
}
