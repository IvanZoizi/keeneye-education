package keenay.education.service.impl;

import keenay.education.dto.chat.ChatDTO;
import keenay.education.dto.chat.MessageDTO;
import keenay.education.entity.*;
import keenay.education.exception.errors.AdvertisementResponseNotFoundException;
import keenay.education.exception.errors.ChatAlreadyCreated;
import keenay.education.exception.errors.ChatNotFoundException;
import keenay.education.mapper.chat.ChatMapper;
import keenay.education.repository.jpa.AdvertisementResponseRepository;
import keenay.education.repository.jpa.ChatRepository;
import keenay.education.repository.jpa.MessagesRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final AdvertisementResponseRepository advertisementResponseRepository;
    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final MessagesRepository messagesRepository;

    private Chat create(Long customerId, Long sellerId) {
        Chat chat = Chat.builder()
                .customerId(customerId)
                .sellerId(sellerId)
                .build();
        return chatRepository.save(chat);
    }

    @Override
    public ChatDTO createChat(CustomUserDetail customUserDetail, Long advertisementResponseId,
                              Long advertisementId) {
        AdvertisementResponse advertisementResponse = advertisementResponseRepository
                .findByIdAndAdvertisement_Id(advertisementResponseId, advertisementId)
                .orElseThrow(() -> new AdvertisementResponseNotFoundException("Advertisement Response is not found."));
        Optional<Chat> chat = chatRepository.findByCustomer_IdAndSeller_Id(customUserDetail.getUserId(),
                advertisementResponse.getSeller().getUser().getId());
        if (chat.isPresent()) {
            throw new ChatAlreadyCreated("Chat is already created");
        }
        return chatMapper.getDTO(create(
                customUserDetail.getUserId(),
                advertisementResponse.getSeller().getUser().getId()
        ));
    }

    @Override
    public ChatDTO getChat(CustomUserDetail customUserDetail, Long chatId) {

        Chat chat = chatRepository.findMessages(chatId, customUserDetail.getUserId())
                .orElseThrow(() -> new ChatNotFoundException("Chat is not found"));

        return chatMapper.getDTO(
                chat
        );
    }

    @Override
    public List<ChatDTO> getChats(CustomUserDetail customUserDetail) {
        return chatRepository.findAllByCustomer_IdOrSeller_Id(customUserDetail.getUserId(),
                        customUserDetail.getUserId())
                .stream()
                .map(chatMapper::getDTO)
                .toList();
    }

    @Override
    @Transactional
    public Page<MessageDTO> getMessages(CustomUserDetail customUserDetail, Long chatId, Pageable pageable) {
        Chat chat = chatRepository.findMessages(chatId, customUserDetail.getUserId())
                .orElseThrow(() -> new ChatNotFoundException("Chat is not found"));

        messagesRepository.markAsRead(chatId, customUserDetail.getUserId(), LocalDateTime.now());

        return messagesRepository.getMessagesByChat_IdOrderByCreatedAtDesc(chatId, pageable)
                .map(chatMapper::getDTO);
    }
}
