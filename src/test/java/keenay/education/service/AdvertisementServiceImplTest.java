package keenay.education.service;

import keenay.education.dto.advertisement.AdvertisementBodyDTO;
import keenay.education.dto.advertisement.AdvertisementBodyStatusDTO;
import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyStatusDTO;
import keenay.education.entity.*;
import keenay.education.entity.status.AdvertisementResponseStatus;
import keenay.education.entity.status.AdvertisementStatus;
import keenay.education.exception.errors.*;
import keenay.education.mapper.advertisement.AdvertisementMapper;
import keenay.education.repository.jpa.AdvertisementRepository;
import keenay.education.repository.jpa.AdvertisementResponseRepository;
import keenay.education.repository.jpa.PetsRepository;
import keenay.education.repository.jpa.TasksRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.AdvertisementServiceImpl;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdvertisementServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long CUSTOMER_ID = 2L;
    private static final Long SELLER_ID = 3L;
    private static final Long AD_ID = 10L;
    private static final Long PET_ID = 20L;

    @Mock
    private PetsRepository petsRepository;

    @Mock
    private TaskService taskService;

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock
    private AdvertisementMapper advertisementMapper;

    @Mock
    private AdvertisementResponseRepository advertisementResponseRepository;

    @Mock
    private AdvertisementResponseService advertisementResponseService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @InjectMocks
    private AdvertisementServiceImpl advertisementService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getUser().getId()).thenReturn(USER_ID);
        lenient().when(userDetail.getUser().getCustomer().getId()).thenReturn(CUSTOMER_ID);
        lenient().when(userDetail.getUser().getSeller().getId()).thenReturn(SELLER_ID);
    }

    private Advertisement advertisement(Long id) {
        Advertisement advertisement = new Advertisement();
        advertisement.setId(id);
        advertisement.setBudget(1500);
        return advertisement;
    }

    private AdvertisementDTO dto(Long id) {
        AdvertisementDTO dto = new AdvertisementDTO();
        dto.setId(id);
        dto.setBudget(1500);
        return dto;
    }

    private AdvertisementBodyDTO body(List<Long> taskIds) {
        AdvertisementBodyDTO body = new AdvertisementBodyDTO();
        body.setPetId(PET_ID);
        body.setBudget(1500);
        body.setListTasksId(taskIds);
        return body;
    }

    private Tasks task(Long id, Advertisement advertisement) {
        Tasks task = new Tasks();
        task.setId(id);
        task.setAdvertisement(advertisement);
        return task;
    }

    @Test
    @DisplayName("Тест createAdvertisement")
    public void testCreateAdvertisementWithCorrectValues() {
        Pets pet = new Pets();
        pet.setId(PET_ID);

        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.of(pet));
        when(advertisementRepository.save(any(Advertisement.class))).thenAnswer(invocation -> {
            Advertisement advertisement = invocation.getArgument(0);
            assertEquals(pet, advertisement.getPet());
            assertEquals(1500, advertisement.getBudget());
            assertNotNull(advertisement.getCustomer());
            advertisement.setId(AD_ID);
            return advertisement;
        });
        when(advertisementMapper.getDTO(any(Advertisement.class))).thenReturn(dto(AD_ID));

        AdvertisementDTO result = advertisementService.createAdvertisement(userDetail, body(List.of(100L, 101L)));

        assertNotNull(result);
        assertEquals(AD_ID, result.getId());
        verify(advertisementRepository).save(any(Advertisement.class));
        verify(taskService).setAdvertisement(userDetail, 100L, AD_ID);
        verify(taskService).setAdvertisement(userDetail, 101L, AD_ID);
        verify(taskService, times(2)).setAdvertisement(eq(userDetail), anyLong(), eq(AD_ID));
    }

    @Test
    @DisplayName("Тест createAdvertisement без задач")
    public void testCreateAdvertisementWithoutTasks() {
        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.of(new Pets()));
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(advertisement(AD_ID));
        when(advertisementMapper.getDTO(any(Advertisement.class))).thenReturn(dto(AD_ID));

        advertisementService.createAdvertisement(userDetail, body(List.of()));

        verify(advertisementRepository).save(any(Advertisement.class));
        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("Тест createAdvertisement null")
    public void testCreateAdvertisementPetNotFound() {
        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.empty());

        assertThrows(PetsNotFoundException.class,
                () -> advertisementService.createAdvertisement(userDetail, body(List.of(100L))));
        verify(advertisementRepository, never()).save(any(Advertisement.class));
        verifyNoInteractions(taskService, advertisementMapper);
    }


    @Test
    @DisplayName("Тест getAdvertisement")
    public void testGetAdvertisementSuccess() {
        Advertisement advertisement = advertisement(AD_ID);
        AdvertisementDTO expectedDto = dto(AD_ID);

        when(advertisementRepository.findByIdAndCustomer_Id(AD_ID, CUSTOMER_ID)).thenReturn(Optional.of(advertisement));
        when(advertisementMapper.getDTO(advertisement)).thenReturn(expectedDto);

        AdvertisementDTO result = advertisementService.getAdvertisement(userDetail, AD_ID);

        assertNotNull(result);
        assertEquals(expectedDto, result);
        assertEquals(AD_ID, result.getId());
        assertEquals(1500, result.getBudget());
        verify(advertisementRepository).findByIdAndCustomer_Id(AD_ID, CUSTOMER_ID);
        verify(advertisementMapper).getDTO(advertisement);
    }

    @Test
    @DisplayName("Тест getAdvertisement null")
    public void testGetAdvertisementNotFound() {
        when(advertisementRepository.findByIdAndCustomer_Id(9999L, CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.getAdvertisement(userDetail, 9999L));
        verify(advertisementRepository).findByIdAndCustomer_Id(9999L, CUSTOMER_ID);
        verify(advertisementMapper, never()).getDTO(any(Advertisement.class));
    }

    @Test
    @DisplayName("Тест getAdvertisements")
    public void testGetAdvertisementsSuccess() {
        Advertisement first = advertisement(1L);
        Advertisement second = advertisement(2L);

        when(advertisementRepository.findAllByCustomer_Id(CUSTOMER_ID)).thenReturn(List.of(first, second));
        when(advertisementMapper.getDTO(first)).thenReturn(dto(1L));
        when(advertisementMapper.getDTO(second)).thenReturn(dto(2L));

        List<AdvertisementDTO> result = advertisementService.getAdvertisements(userDetail);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(advertisementRepository).findAllByCustomer_Id(CUSTOMER_ID);
        verify(advertisementMapper, times(2)).getDTO(any(Advertisement.class));
    }

    @Test
    @DisplayName("Тест getAdvertisements пустой список")
    public void testGetAdvertisementsEmpty() {
        when(advertisementRepository.findAllByCustomer_Id(CUSTOMER_ID)).thenReturn(List.of());

        List<AdvertisementDTO> result = advertisementService.getAdvertisements(userDetail);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(advertisementMapper);
    }

    @Test
    @DisplayName("Тест getAdvertisementBySkills")
    public void testGetAdvertisementBySkills() {
        Advertisement advertisement = advertisement(AD_ID);

        when(advertisementRepository.findAdvertisementBySkills(SELLER_ID)).thenReturn(List.of(advertisement));
        when(advertisementMapper.getDTO(advertisement)).thenReturn(dto(AD_ID));

        List<AdvertisementDTO> result = advertisementService.getAdvertisementBySkills(userDetail);

        assertEquals(1, result.size());
        assertEquals(AD_ID, result.get(0).getId());
        verify(advertisementRepository).findAdvertisementBySkills(SELLER_ID);
    }

    @Test
    @DisplayName("Тест updateStatus")
    public void testUpdateStatusSuccess() {
        Advertisement updated = advertisement(AD_ID);
        updated.setStatus(AdvertisementStatus.PROGRESS);
        AdvertisementDTO expectedDto = dto(AD_ID);
        expectedDto.setAdvertisementStatus(AdvertisementStatus.PROGRESS);

        when(advertisementRepository.updateStatus(AD_ID, CUSTOMER_ID, "PROGRESS")).thenReturn(List.of(updated));
        when(advertisementMapper.getDTO(updated)).thenReturn(expectedDto);

        AdvertisementDTO result = advertisementService.updateStatus(
                userDetail, AD_ID, new AdvertisementBodyStatusDTO(AdvertisementStatus.PROGRESS));

        assertEquals(AdvertisementStatus.PROGRESS, result.getAdvertisementStatus());
        verify(advertisementRepository).updateStatus(AD_ID, CUSTOMER_ID, "PROGRESS");
        verify(advertisementMapper).getDTO(updated);
    }

    @Test
    @DisplayName("Тест updateStatus null")
    public void testUpdateStatusNotFound() {
        when(advertisementRepository.updateStatus(9999L, CUSTOMER_ID, "PROGRESS")).thenReturn(List.of());

        assertThrows(AdvertisementNotFoundException.class,
                () -> advertisementService.updateStatus(
                        userDetail, 9999L, new AdvertisementBodyStatusDTO(AdvertisementStatus.PROGRESS)));
        verify(advertisementMapper, never()).getDTO(any(Advertisement.class));
    }

    @Test
    @DisplayName("Тест addTask")
    public void testAddTaskSuccess() {
        Advertisement advertisement = advertisement(AD_ID);

        when(tasksRepository.findById(100L)).thenReturn(Optional.of(task(100L, null)));
        when(advertisementRepository.findByIdAndCustomer_Id(AD_ID, CUSTOMER_ID)).thenReturn(Optional.of(advertisement));
        when(advertisementMapper.getDTO(advertisement)).thenReturn(dto(AD_ID));

        AdvertisementDTO result = advertisementService.addTask(userDetail, AD_ID, 100L);

        assertEquals(AD_ID, result.getId());
        verify(taskService).setAdvertisement(userDetail, 100L, AD_ID);
    }

    @Test
    @DisplayName("Тест addTask null")
    public void testAddTaskNotFound() {
        when(tasksRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> advertisementService.addTask(userDetail, AD_ID, 9999L));
        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("Тест addTask задача уже в этом объявлении")
    public void testAddTaskAlreadyInAdvertisement() {
        when(tasksRepository.findById(100L)).thenReturn(Optional.of(task(100L, advertisement(AD_ID))));

        assertThrows(TaskBusyException.class,
                () -> advertisementService.addTask(userDetail, AD_ID, 100L));
        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("Тест deleteTask")
    public void testDeleteTaskSuccess() {
        Advertisement advertisement = advertisement(AD_ID);

        when(tasksRepository.findById(100L)).thenReturn(Optional.of(task(100L, advertisement)));
        when(advertisementRepository.findByIdAndCustomer_Id(AD_ID, CUSTOMER_ID)).thenReturn(Optional.of(advertisement));
        when(advertisementMapper.getDTO(advertisement)).thenReturn(dto(AD_ID));

        AdvertisementDTO result = advertisementService.deleteTask(userDetail, AD_ID, 100L);

        assertEquals(AD_ID, result.getId());
        verify(taskService).deleteTask(userDetail, 100L);
    }

    @Test
    @DisplayName("Тест deleteTask задача в другом объявлении")
    public void testDeleteTaskFromOtherAdvertisement() {
        when(tasksRepository.findById(100L)).thenReturn(Optional.of(task(100L, advertisement(777L))));

        assertThrows(TaskBusyException.class,
                () -> advertisementService.deleteTask(userDetail, AD_ID, 100L));
        verify(taskService, never()).deleteTask(any(), anyLong());
    }

    @Test
    @DisplayName("Тест deleteTask null")
    public void testDeleteTaskNotFound() {
        when(tasksRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> advertisementService.deleteTask(userDetail, AD_ID, 9999L));
        verifyNoInteractions(taskService);
    }

    @Test
    @DisplayName("Тест setResponse")
    public void testSetResponseSuccess() {
        AdvertisementResponse selected = new AdvertisementResponse();
        selected.setId(50L);
        selected.setStatus(AdvertisementResponseStatus.CREATED);

        AdvertisementResponse other = new AdvertisementResponse();
        other.setId(51L);
        other.setStatus(AdvertisementResponseStatus.CREATED);

        Advertisement advertisement = advertisement(AD_ID);
        advertisement.setResponses(List.of(other));
        Advertisement updated = advertisement(AD_ID);

        when(advertisementResponseRepository.findByIdAndSeller_Id(50L, SELLER_ID)).thenReturn(Optional.of(selected));
        when(advertisementRepository.findByIdAndCustomer_Id(AD_ID, CUSTOMER_ID)).thenReturn(Optional.of(advertisement));
        when(advertisementRepository.updateStatus(AD_ID, CUSTOMER_ID, "PROGRESS")).thenReturn(List.of(updated));
        when(advertisementMapper.getDTO(updated)).thenReturn(dto(AD_ID));

        AdvertisementDTO result = advertisementService.setResponse(userDetail, AD_ID, 50L);

        assertEquals(AD_ID, result.getId());
        verify(advertisementResponseService).updateStatus(eq(userDetail), eq(50L),
                argThat((AdvertisementResponseBodyStatusDTO s) -> s.getStatus() == AdvertisementResponseStatus.SELECTED));
        verify(advertisementResponseService).updateStatus(eq(userDetail), eq(51L),
                argThat((AdvertisementResponseBodyStatusDTO s) -> s.getStatus() == AdvertisementResponseStatus.REJECTED));
        verify(advertisementRepository).updateStatus(AD_ID, CUSTOMER_ID, "PROGRESS");
    }

    @Test
    @DisplayName("Тест setResponse null")
    public void testSetResponseNotFound() {
        when(advertisementResponseRepository.findByIdAndSeller_Id(9999L, SELLER_ID)).thenReturn(Optional.empty());

        assertThrows(AdvertisementResponseNotFoundException.class,
                () -> advertisementService.setResponse(userDetail, AD_ID, 9999L));
        verifyNoInteractions(advertisementResponseService);
        verify(advertisementRepository, never()).updateStatus(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Тест setResponse отклик уже обработан")
    public void testSetResponseBusy() {
        AdvertisementResponse response = new AdvertisementResponse();
        response.setId(50L);
        response.setStatus(AdvertisementResponseStatus.REJECTED);

        when(advertisementResponseRepository.findByIdAndSeller_Id(50L, SELLER_ID)).thenReturn(Optional.of(response));

        assertThrows(AdvertisementResponseBusyException.class,
                () -> advertisementService.setResponse(userDetail, AD_ID, 50L));
        verifyNoInteractions(advertisementResponseService);
    }

    @Test
    @DisplayName("Тест deleteAdvertisement")
    public void testDeleteAdvertisement() {
        advertisementService.deleteAdvertisement(userDetail, AD_ID);

        verify(advertisementRepository).delete(AD_ID, CUSTOMER_ID);
        verifyNoMoreInteractions(advertisementRepository);
        verifyNoInteractions(advertisementMapper);
    }
}