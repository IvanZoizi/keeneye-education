package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import keenay.education.dto.chat.ChatDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Chat Endpoints")
@RequestMapping("/api/v1/chat")
public interface ChatController {
    ChatDTO createChat(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("advertisementId") Long advertisementId,
            @PathVariable("responseId") Long responseId
    );
}