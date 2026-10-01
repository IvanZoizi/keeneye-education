package keenay.education.service;

import keenay.education.dto.chat.ChatDTO;
import keenay.education.dto.chat.MessageDTO;
import keenay.education.entity.AdvertisementResponse;
import keenay.education.entity.Chat;
import keenay.education.entity.Sellers;
import keenay.education.entity.Users;
import keenay.education.exception.errors.AdvertisementResponseNotFoundException;
import keenay.education.exception.errors.ChatAlreadyCreated;
import keenay.education.exception.errors.ChatNotFoundException;
import keenay.education.mapper.chat.ChatMapper;
import keenay.education.repository.jpa.AdvertisementResponseRepository;
import keenay.education.repository.jpa.ChatRepository;
import keenay.education.repository.jpa.MessagesRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.ChatServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long SELLER_USER_ID = 3L;
    private static final Long AD_ID = 20L;
    private static final Long RESPONSE_ID = 30L;
    private static final Long CHAT_ID = 10L;

    @Mock
    private AdvertisementResponseRepository advertisementResponseRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatMapper chatMapper;

    @Mock
    private MessagesRepository messagesRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @InjectMocks
    private ChatServiceImpl chatService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getUser().getId()).thenReturn(USER_ID);
    }

    private Users user(Long id) {
        Users user = new Users();
        user.setId(id);
        return user;
    }

    private Sellers seller(Long id, Users user) {
        Sellers seller = new Sellers();
        seller.setId(id);
        seller.setUser(user);
        return seller;
    }

    private Chat chat(Long id, Users customer, Users seller) {
        Chat chat = new Chat();
        chat.setId(id);
        chat.setCustomer(customer);
        chat.setSeller(seller);
        return chat;
    }

    private ChatDTO chatDto(Long id) {
        ChatDTO dto = new ChatDTO();
        dto.setChatId(id);
        dto.setSellerId(3L);
        dto.setCustomerId(2L);
        return dto;
    }

    private MessageDTO messageDto(Long id) {
        MessageDTO dto = new MessageDTO();
        dto.setId(id);
        dto.setChatId(CHAT_ID);
        dto.setSenderId(USER_ID);
        dto.setRole("ROLE_customer");
        dto.setText("Text");
        return dto;
    }

    @Test
    @DisplayName("Тест createChat")
    public void testCreateChatSuccess() {
        Users customer = user(USER_ID);
        Users sellerUser = user(SELLER_USER_ID);
        Sellers seller = seller(5L, sellerUser);
        AdvertisementResponse response = new AdvertisementResponse();
        response.setId(RESPONSE_ID);
        response.setSeller(seller);
        ChatDTO expected = chatDto(CHAT_ID);

        when(userDetail.getUser()).thenReturn(customer);
        when(advertisementResponseRepository.findByIdAndAdvertisement_Id(RESPONSE_ID, AD_ID))
                .thenReturn(Optional.of(response));
        when(chatRepository.findByCustomer_IdAndSeller_Id(USER_ID, SELLER_USER_ID))
                .thenReturn(Optional.empty());
        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> {
            Chat c = invocation.getArgument(0);
            assertEquals(customer, c.getCustomer());
            assertEquals(sellerUser, c.getSeller());
            c.setId(CHAT_ID);
            return c;
        });
        when(chatMapper.getDTO(any(Chat.class))).thenReturn(expected);

        ChatDTO result = chatService.createChat(userDetail, RESPONSE_ID, AD_ID);

        assertNotNull(result);
        assertEquals(CHAT_ID, result.getChatId());
        verify(advertisementResponseRepository).findByIdAndAdvertisement_Id(RESPONSE_ID, AD_ID);
        verify(chatRepository).findByCustomer_IdAndSeller_Id(USER_ID, SELLER_USER_ID);
        verify(chatRepository).save(any(Chat.class));
        verify(chatMapper).getDTO(any(Chat.class));
    }

    @Test
    @DisplayName("Тест createChat отклик не найден")
    public void testCreateChatResponseNotFound() {
        when(advertisementResponseRepository.findByIdAndAdvertisement_Id(RESPONSE_ID, AD_ID))
                .thenReturn(Optional.empty());

        assertThrows(AdvertisementResponseNotFoundException.class,
                () -> chatService.createChat(userDetail, RESPONSE_ID, AD_ID));
        verify(chatRepository, never()).save(any(Chat.class));
        verifyNoInteractions(chatMapper);
    }

    @Test
    @DisplayName("Тест createChat чат уже существует")
    public void testCreateChatAlreadyExists() {
        Users customer = user(USER_ID);
        Users sellerUser = user(SELLER_USER_ID);
        Sellers seller = seller(5L, sellerUser);
        AdvertisementResponse response = new AdvertisementResponse();
        response.setId(RESPONSE_ID);
        response.setSeller(seller);

        when(userDetail.getUser()).thenReturn(customer);
        when(advertisementResponseRepository.findByIdAndAdvertisement_Id(RESPONSE_ID, AD_ID))
                .thenReturn(Optional.of(response));
        when(chatRepository.findByCustomer_IdAndSeller_Id(USER_ID, SELLER_USER_ID))
                .thenReturn(Optional.of(chat(CHAT_ID, customer, sellerUser)));

        assertThrows(ChatAlreadyCreated.class,
                () -> chatService.createChat(userDetail, RESPONSE_ID, AD_ID));
        verify(chatRepository, never()).save(any(Chat.class));
        verifyNoInteractions(chatMapper);
    }

    @Test
    @DisplayName("Тест getChat")
    public void testGetChatSuccess() {
        Chat chat = chat(CHAT_ID, user(USER_ID), user(SELLER_USER_ID));
        ChatDTO expected = chatDto(CHAT_ID);

        when(chatRepository.findMessages(CHAT_ID, USER_ID)).thenReturn(Optional.of(chat));
        when(chatMapper.getDTO(chat)).thenReturn(expected);

        ChatDTO result = chatService.getChat(userDetail, CHAT_ID);

        assertNotNull(result);
        assertEquals(CHAT_ID, result.getChatId());
        verify(chatRepository).findMessages(CHAT_ID, USER_ID);
        verify(chatMapper).getDTO(chat);
    }

    @Test
    @DisplayName("Тест getChat null")
    public void testGetChatNotFound() {
        when(chatRepository.findMessages(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.getChat(userDetail, 9999L));
        verify(chatMapper, never()).getDTO(any(Chat.class));
    }

    @Test
    @DisplayName("Тест getChats")
    public void testGetChatsSuccess() {
        Chat first = chat(1L, user(USER_ID), user(SELLER_USER_ID));
        Chat second = chat(2L, user(USER_ID), user(SELLER_USER_ID));

        when(chatRepository.findAllByCustomer_IdOrSeller_Id(USER_ID, USER_ID))
                .thenReturn(List.of(first, second));
        when(chatMapper.getDTO(first)).thenReturn(chatDto(1L));
        when(chatMapper.getDTO(second)).thenReturn(chatDto(2L));

        List<ChatDTO> result = chatService.getChats(userDetail);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getChatId());
        assertEquals(2L, result.get(1).getChatId());
        verify(chatRepository).findAllByCustomer_IdOrSeller_Id(USER_ID, USER_ID);
        verify(chatMapper, times(2)).getDTO(any(Chat.class));
    }

    @Test
    @DisplayName("Тест getChats пустой список")
    public void testGetChatsEmpty() {
        when(chatRepository.findAllByCustomer_IdOrSeller_Id(USER_ID, USER_ID)).thenReturn(List.of());

        List<ChatDTO> result = chatService.getChats(userDetail);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(chatMapper);
    }


    @Test
    @DisplayName("Тест getMessages null")
    public void testGetMessagesChatNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(chatRepository.findMessages(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(ChatNotFoundException.class,
                () -> chatService.getMessages(userDetail, 9999L, pageable));
        verifyNoInteractions(messagesRepository);
    }
}