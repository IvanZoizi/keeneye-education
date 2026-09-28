package keenay.education.controllers.http.impl;

import keenay.education.controllers.http.ChatController;
import keenay.education.dto.chat.ChatDTO;
import keenay.education.dto.chat.MessageDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatControllerImpl implements ChatController {

    private final ChatService chatService;

    @PostMapping("/advertisement/{advertisementId}/response/{responseId}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public ChatDTO createChat(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long advertisementId,
            @PathVariable Long responseId
    ) {
        return chatService.createChat(userDetail, responseId, advertisementId);
    }

    @GetMapping("/{id}")
    public ChatDTO getChat(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return chatService.getChat(userDetail, id);
    }

    @GetMapping
    public List<ChatDTO> getChats(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return chatService.getChats(userDetail);
    }

    @GetMapping("/{chatId}/messages")
    public Page<MessageDTO> getMessages(
            @PathVariable Long chatId,
            @AuthenticationPrincipal CustomUserDetail user,
            Pageable pageable
    ) {
        return chatService.getMessages(user, chatId, pageable);
    }
}