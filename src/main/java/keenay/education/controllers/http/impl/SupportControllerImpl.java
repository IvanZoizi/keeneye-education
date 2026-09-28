package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.SupportController;
import keenay.education.dto.support.TicketAnswerBodyDTO;
import keenay.education.dto.support.TicketBodyDTO;
import keenay.education.dto.support.TicketDTO;
import keenay.education.dto.support.TicketWithAnswerDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class SupportControllerImpl implements SupportController {

    private final SupportService supportService;

    @PostMapping
    public TicketWithAnswerDTO createTicket(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody TicketBodyDTO ticketBodyDTO
    ) {
        return supportService.createTicket(userDetail, ticketBodyDTO);
    }

    @GetMapping("/{id}")
    public TicketWithAnswerDTO getTicketInfo(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id
    ) {
        return supportService.getTicketInfo(customUserDetail, id);
    }

    @PostMapping("/answer/{id}")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public TicketWithAnswerDTO answerForTicket(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id,
            @Valid @RequestBody TicketAnswerBodyDTO ticketAnswerBodyDTO
    ) {
        return supportService.answerForTicket(customUserDetail, id, ticketAnswerBodyDTO);
    }

    @GetMapping("/tickets")
    @PreAuthorize("hasAuthority('ROLE_admin')")
    public List<TicketDTO> getAvailableTicket() {
        return supportService.getAvailableTicket();
    }
}