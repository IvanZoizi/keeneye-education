package keenay.education.service;

import keenay.education.dto.support.TicketAnswerBodyDTO;
import keenay.education.dto.support.TicketBodyDTO;
import keenay.education.dto.support.TicketDTO;
import keenay.education.dto.support.TicketWithAnswerDTO;
import keenay.education.entity.Ticket;
import keenay.education.entity.TicketReplies;
import keenay.education.entity.Users;
import keenay.education.exception.errors.TicketHasNotBeenCreatedException;
import keenay.education.mapper.ticket.TicketMapper;
import keenay.education.repository.jpa.TicketRepliesRepository;
import keenay.education.repository.jpa.TicketRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.SupportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SupportServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long TICKET_ID = 10L;

    @Mock
    private TicketMapper mapperService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketRepliesRepository ticketRepliesRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @InjectMocks
    private SupportServiceImpl supportService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getUser().getId()).thenReturn(USER_ID);
    }

    private Users user(Long id) {
        Users user = new Users();
        user.setId(id);
        return user;
    }

    private Ticket ticket(Long id, String question) {
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setQuestion(question);
        return ticket;
    }

    private TicketBodyDTO ticketBody() {
        TicketBodyDTO body = new TicketBodyDTO();
        body.setQuestion("Question");
        return body;
    }

    private TicketAnswerBodyDTO answerBody() {
        TicketAnswerBodyDTO body = new TicketAnswerBodyDTO();
        body.setAnswer("Answer");
        return body;
    }

    private TicketWithAnswerDTO ticketWithAnswer(Long id) {
        TicketWithAnswerDTO dto = new TicketWithAnswerDTO();
        dto.setId(id);
        dto.setQuestion("Question");
        dto.setAnswer("Answer");
        return dto;
    }

    private TicketDTO ticketDto(Long id) {
        TicketDTO dto = new TicketDTO();
        dto.setId(id);
        dto.setQuestion("Question");
        return dto;
    }

    @Test
    @DisplayName("Тест createTicket")
    public void testCreateTicketSuccess() {
        Users user = user(USER_ID);
        when(userDetail.getUser()).thenReturn(user);

        TicketWithAnswerDTO expected = ticketWithAnswer(TICKET_ID);

        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            assertEquals("Question", t.getQuestion());
            assertEquals(user, t.getSender());
            assertNull(t.getAdmin());
            t.setId(TICKET_ID);
            return t;
        });
        when(mapperService.getTicketWithAnswerDTO(any(Ticket.class))).thenReturn(expected);

        TicketWithAnswerDTO result = supportService.createTicket(userDetail, ticketBody());

        assertNotNull(result);
        assertEquals(TICKET_ID, result.getId());
        verify(ticketRepository).save(any(Ticket.class));
        verify(mapperService).getTicketWithAnswerDTO(any(Ticket.class));
    }

    @Test
    @DisplayName("Тест getTicketInfo")
    public void testGetTicketInfoSuccess() {
        Ticket ticket = ticket(TICKET_ID, "Question");
        TicketWithAnswerDTO expected = ticketWithAnswer(TICKET_ID);

        when(ticketRepository.findByIdAndSender_Id(TICKET_ID, USER_ID)).thenReturn(Optional.of(ticket));
        when(mapperService.getTicketWithAnswerDTO(ticket)).thenReturn(expected);

        TicketWithAnswerDTO result = supportService.getTicketInfo(userDetail, TICKET_ID);

        assertNotNull(result);
        assertEquals(TICKET_ID, result.getId());
        verify(ticketRepository).findByIdAndSender_Id(TICKET_ID, USER_ID);
        verify(mapperService).getTicketWithAnswerDTO(ticket);
    }

    @Test
    @DisplayName("Тест getTicketInfo null")
    public void testGetTicketInfoNotFound() {
        when(ticketRepository.findByIdAndSender_Id(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(TicketHasNotBeenCreatedException.class,
                () -> supportService.getTicketInfo(userDetail, 9999L));
        verify(mapperService, never()).getTicketWithAnswerDTO(any(Ticket.class));
    }

    @Test
    @DisplayName("Тест answerForTicket")
    public void testAnswerForTicketSuccess() {
        Ticket ticket = ticket(TICKET_ID, "Question");
        Users admin = user(2L);
        TicketWithAnswerDTO expected = ticketWithAnswer(TICKET_ID);

        when(userDetail.getUser()).thenReturn(admin);
        when(ticketRepository.findById(TICKET_ID)).thenReturn(Optional.of(ticket));
        when(ticketRepliesRepository.save(any(TicketReplies.class))).thenAnswer(invocation -> {
            TicketReplies replies = invocation.getArgument(0);
            assertEquals("Answer", replies.getAnswer());
            assertEquals(ticket, replies.getTicket());
            return replies;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> {
            Ticket t = invocation.getArgument(0);
            assertEquals(admin, t.getAdmin());
            return t;
        });
        when(mapperService.getTicketWithAnswerDTO(any(Ticket.class))).thenReturn(expected);

        TicketWithAnswerDTO result = supportService.answerForTicket(userDetail, TICKET_ID, answerBody());

        assertNotNull(result);
        assertEquals(TICKET_ID, result.getId());
        assertEquals(admin, ticket.getAdmin());
        verify(ticketRepository).findById(TICKET_ID);
        verify(ticketRepliesRepository).save(any(TicketReplies.class));
        verify(ticketRepository).save(ticket);
    }

    @Test
    @DisplayName("Тест answerForTicket null")
    public void testAnswerForTicketNotFound() {
        when(ticketRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(TicketHasNotBeenCreatedException.class,
                () -> supportService.answerForTicket(userDetail, 9999L, answerBody()));
        verifyNoInteractions(ticketRepliesRepository);
        verify(ticketRepository, never()).save(any(Ticket.class));
    }

    @Test
    @DisplayName("Тест getAvailableTicket")
    public void testGetAvailableTicketSuccess() {
        Ticket first = ticket(1L, "Question1");
        Ticket second = ticket(2L, "Question2");

        when(ticketRepository.findAll()).thenReturn(List.of(first, second));
        when(mapperService.getTicketDTO(first)).thenReturn(ticketDto(1L));
        when(mapperService.getTicketDTO(second)).thenReturn(ticketDto(2L));

        List<TicketDTO> result = supportService.getAvailableTicket();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(ticketRepository).findAll();
        verify(mapperService, times(2)).getTicketDTO(any(Ticket.class));
    }

    @Test
    @DisplayName("Тест getAvailableTicket пустой список")
    public void testGetAvailableTicketEmpty() {
        when(ticketRepository.findAll()).thenReturn(List.of());

        List<TicketDTO> result = supportService.getAvailableTicket();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(mapperService);
    }
}