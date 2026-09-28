package keenay.education.service.impl;

import keenay.education.dto.support.TicketAnswerBodyDTO;
import keenay.education.dto.support.TicketBodyDTO;
import keenay.education.dto.support.TicketDTO;
import keenay.education.dto.support.TicketWithAnswerDTO;
import keenay.education.entity.Ticket;
import keenay.education.entity.TicketReplies;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.exception.errors.TicketHasNotBeenCreatedException;
import keenay.education.mapper.ticket.TicketMapper;
import keenay.education.repository.TicketRepliesRepository;
import keenay.education.repository.TicketRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SupportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final TicketMapper mapperService;
    private final TicketRepository ticketRepository;
    private final TicketRepliesRepository ticketRepliesRepository;


    @Override
    public TicketWithAnswerDTO createTicket(CustomUserDetail customUserDetail, TicketBodyDTO ticketBodyDTO) {
        Ticket ticket = Ticket.builder()
                .question(ticketBodyDTO.getQuestion())
                .sender(customUserDetail.getUser())
                .build();
        return mapperService.getTicketWithAnswerDTO(ticketRepository.save(ticket));
    }

    @Override
    public TicketWithAnswerDTO getTicketInfo(CustomUserDetail customUserDetail, Long id) {
        Ticket ticket = ticketRepository.findByIdAndSender_Id(id, customUserDetail.getUser().getId())
                .orElseThrow(() -> new TicketHasNotBeenCreatedException("The ticket was not found."));
        return mapperService.getTicketWithAnswerDTO(ticket);
    }

    @Override
    public TicketWithAnswerDTO answerForTicket(CustomUserDetail customUserDetail, Long id, TicketAnswerBodyDTO ticketAnswerBodyDTO) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketHasNotBeenCreatedException("The ticket was not found."));
        TicketReplies ticketReplies = TicketReplies.builder()
                .answer(ticketAnswerBodyDTO.getAnswer())
                .ticket(ticket)
                .build();
        ticketRepliesRepository.save(ticketReplies);
        ticket.setAdmin(customUserDetail.getUser());
        return mapperService.getTicketWithAnswerDTO(ticketRepository.save(ticket));
    }

    @Override
    public List<TicketDTO> getAvailableTicket() {
        return ticketRepository.findAll().stream()
                .map(mapperService::getTicketDTO)
                .toList();
    }
}
