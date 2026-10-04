package keenay.education.service;

import keenay.education.dto.tasks.TaskBodyDTO;
import keenay.education.dto.tasks.TaskBodyStatusDTO;
import keenay.education.dto.tasks.TaskDTO;
import keenay.education.entity.Advertisement;
import keenay.education.entity.Tasks;
import keenay.education.entity.status.TasksStatus;
import keenay.education.exception.errors.AdvertisementNotFoundException;
import keenay.education.exception.errors.TaskBusyException;
import keenay.education.exception.errors.TaskNotFoundException;
import keenay.education.mapper.tasks.TaskMapper;
import keenay.education.repository.jpa.AdvertisementRepository;
import keenay.education.repository.jpa.TasksRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.image.ImageService;
import keenay.education.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    private static final Long CUSTOMER_ID = 2L;
    private static final Long TASK_ID = 10L;
    private static final Long AD_ID = 20L;
    private static final String PHOTO_URL = "http://minio/photo.jpg";

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private TasksRepository tasksRepository;

    @Mock
    private ImageService imageService;

    @Mock
    private AdvertisementRepository advertisementRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private TaskServiceImpl taskService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getCustomerId()).thenReturn(CUSTOMER_ID);
    }

    private Tasks task(Long id, TasksStatus status) {
        Tasks task = new Tasks();
        task.setId(id);
        task.setTitle("Title");
        task.setDescription("Description");
        task.setStatus(status);
        return task;
    }

    private TaskDTO dto(Long id, TasksStatus status) {
        TaskDTO dto = new TaskDTO();
        dto.setId(id);
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setStatus(status);
        return dto;
    }

    private TaskBodyDTO body() {
        TaskBodyDTO body = new TaskBodyDTO();
        body.setTitle("Title");
        body.setDescription("Description");
        return body;
    }

    private Advertisement advertisement(Long id) {
        Advertisement advertisement = new Advertisement();
        advertisement.setId(id);
        return advertisement;
    }

    @Test
    @DisplayName("Тест createTask с фото")
    public void testCreateTaskWithPhoto() {
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(multipartFile.isEmpty()).thenReturn(false);
        when(imageService.uploadPhoto(multipartFile, userDetail)).thenReturn(PHOTO_URL);
        when(tasksRepository.save(any(Tasks.class))).thenAnswer(invocation -> {
            Tasks t = invocation.getArgument(0);
            assertEquals("Title", t.getTitle());
            assertEquals("Description", t.getDescription());
            assertEquals(PHOTO_URL, t.getPhotoUrl());
            assertNotNull(t.getCustomer());
            t.setId(TASK_ID);
            return t;
        });
        when(taskMapper.getDTO(any(Tasks.class))).thenReturn(expected);

        TaskDTO result = taskService.createTask(userDetail, body(), multipartFile);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verify(imageService).uploadPhoto(multipartFile, userDetail);
        verify(tasksRepository).save(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест createTask без фото")
    public void testCreateTaskWithoutPhoto() {
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(multipartFile.isEmpty()).thenReturn(true);
        when(tasksRepository.save(any(Tasks.class))).thenAnswer(invocation -> {
            Tasks t = invocation.getArgument(0);
            assertNull(t.getPhotoUrl());
            t.setId(TASK_ID);
            return t;
        });
        when(taskMapper.getDTO(any(Tasks.class))).thenReturn(expected);

        TaskDTO result = taskService.createTask(userDetail, body(), multipartFile);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verifyNoInteractions(imageService);
        verify(tasksRepository).save(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест getTask")
    public void testGetTaskSuccess() {
        Tasks task = task(TASK_ID, TasksStatus.CREATED);
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(tasksRepository.findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID)).thenReturn(Optional.of(task));
        when(taskMapper.getDTO(task)).thenReturn(expected);

        TaskDTO result = taskService.getTask(userDetail, TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verify(tasksRepository).findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID);
        verify(taskMapper).getDTO(task);
    }

    @Test
    @DisplayName("Тест getTask null")
    public void testGetTaskNotFound() {
        when(tasksRepository.findByIdAndCustomer_Id(9999L, CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.getTask(userDetail, 9999L));
        verify(taskMapper, never()).getDTO(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест getAvailTasks")
    public void testGetAvailTasksSuccess() {
        Tasks first = task(1L, TasksStatus.CREATED);
        Tasks second = task(2L, TasksStatus.PROGRESS);

        when(tasksRepository.findAllByCustomer_Id(CUSTOMER_ID)).thenReturn(List.of(first, second));
        when(taskMapper.getDTO(first)).thenReturn(dto(1L, TasksStatus.CREATED));
        when(taskMapper.getDTO(second)).thenReturn(dto(2L, TasksStatus.PROGRESS));

        List<TaskDTO> result = taskService.getAvailTasks(userDetail);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(tasksRepository).findAllByCustomer_Id(CUSTOMER_ID);
        verify(taskMapper, times(2)).getDTO(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест getAvailTasks пустой список")
    public void testGetAvailTasksEmpty() {
        when(tasksRepository.findAllByCustomer_Id(CUSTOMER_ID)).thenReturn(List.of());

        List<TaskDTO> result = taskService.getAvailTasks(userDetail);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(taskMapper);
    }

    @Test
    @DisplayName("Тест getCreatedTasks")
    public void testGetCreatedTasksSuccess() {
        Tasks task = task(TASK_ID, TasksStatus.CREATED);
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(tasksRepository.findAllByCustomer_IdAndStatus(CUSTOMER_ID, TasksStatus.CREATED))
                .thenReturn(List.of(task));
        when(taskMapper.getDTO(task)).thenReturn(expected);

        List<TaskDTO> result = taskService.getCreatedTasks(userDetail);

        assertEquals(1, result.size());
        assertEquals(TASK_ID, result.get(0).getId());
        verify(tasksRepository).findAllByCustomer_IdAndStatus(CUSTOMER_ID, TasksStatus.CREATED);
    }

    @Test
    @DisplayName("Тест updateTask")
    public void testUpdateTaskSuccess() {
        Tasks updated = task(TASK_ID, TasksStatus.CREATED);
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(tasksRepository.updateTask(TASK_ID, CUSTOMER_ID, "Title", "Description"))
                .thenReturn(List.of(updated));
        when(taskMapper.getDTO(updated)).thenReturn(expected);

        TaskDTO result = taskService.updateTask(userDetail, TASK_ID, body());

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verify(tasksRepository).updateTask(TASK_ID, CUSTOMER_ID, "Title", "Description");
        verify(taskMapper).getDTO(updated);
    }

    @Test
    @DisplayName("Тест updateTask null")
    public void testUpdateTaskNotFound() {
        when(tasksRepository.updateTask(9999L, CUSTOMER_ID, "Title", "Description"))
                .thenReturn(List.of());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTask(userDetail, 9999L, body()));
        verify(taskMapper, never()).getDTO(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест updatePhotoTask")
    public void testUpdatePhotoTaskSuccess() {
        Tasks task = task(TASK_ID, TasksStatus.CREATED);
        Tasks updated = task(TASK_ID, TasksStatus.CREATED);
        updated.setPhotoUrl(PHOTO_URL);
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(tasksRepository.findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID)).thenReturn(Optional.of(task));
        when(imageService.uploadPhoto(multipartFile, userDetail)).thenReturn(PHOTO_URL);
        when(tasksRepository.updateTaskPhoto(TASK_ID, CUSTOMER_ID, PHOTO_URL))
                .thenReturn(List.of(updated));
        when(taskMapper.getDTO(updated)).thenReturn(expected);

        TaskDTO result = taskService.updatePhotoTask(userDetail, TASK_ID, multipartFile);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verify(imageService).uploadPhoto(multipartFile, userDetail);
        verify(tasksRepository).updateTaskPhoto(TASK_ID, CUSTOMER_ID, PHOTO_URL);
    }

    @Test
    @DisplayName("Тест updatePhotoTask null")
    public void testUpdatePhotoTaskNotFound() {
        when(tasksRepository.findByIdAndCustomer_Id(9999L, CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updatePhotoTask(userDetail, 9999L, multipartFile));
        verifyNoInteractions(imageService);
    }

    @Test
    @DisplayName("Тест updateTaskStatus")
    public void testUpdateTaskStatusSuccess() {
        Tasks updated = task(TASK_ID, TasksStatus.PROGRESS);
        TaskDTO expected = dto(TASK_ID, TasksStatus.PROGRESS);

        when(tasksRepository.updateTaskStatus(TASK_ID, CUSTOMER_ID, "PROGRESS"))
                .thenReturn(List.of(updated));
        when(taskMapper.getDTO(updated)).thenReturn(expected);

        TaskDTO result = taskService.updateTaskStatus(
                userDetail, TASK_ID, new TaskBodyStatusDTO(TasksStatus.PROGRESS));

        assertEquals(TasksStatus.PROGRESS, result.getStatus());
        verify(tasksRepository).updateTaskStatus(TASK_ID, CUSTOMER_ID, "PROGRESS");
    }

    @Test
    @DisplayName("Тест updateTaskStatus null")
    public void testUpdateTaskStatusNotFound() {
        when(tasksRepository.updateTaskStatus(9999L, CUSTOMER_ID, "PROGRESS"))
                .thenReturn(List.of());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.updateTaskStatus(
                        userDetail, 9999L, new TaskBodyStatusDTO(TasksStatus.PROGRESS)));
        verify(taskMapper, never()).getDTO(any(Tasks.class));
    }
    @Test
    @DisplayName("Тест setAdvertisement")
    public void testSetAdvertisementSuccess() {
        Advertisement advertisement = advertisement(AD_ID);
        Tasks task = task(TASK_ID, TasksStatus.CREATED);
        Tasks updated = task(TASK_ID, TasksStatus.PROGRESS);
        updated.setAdvertisement(advertisement);
        TaskDTO expected = dto(TASK_ID, TasksStatus.PROGRESS);

        when(advertisementRepository.findById(AD_ID)).thenReturn(Optional.of(advertisement));
        when(tasksRepository.findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID)).thenReturn(Optional.of(task));
        when(tasksRepository.updateTaskStatus(TASK_ID, CUSTOMER_ID, "PROGRESS"))
                .thenReturn(List.of(updated));
        when(tasksRepository.save(any(Tasks.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(taskMapper.getDTO(any(Tasks.class))).thenReturn(expected);

        TaskDTO result = taskService.setAdvertisement(userDetail, TASK_ID, AD_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        assertNotNull(task.getAdvertisement());
        verify(advertisementRepository).findById(AD_ID);
        verify(tasksRepository).save(task);
    }

    @Test
    @DisplayName("Тест setAdvertisement объявление не найдено")
    public void testSetAdvertisementAdvertisementNotFound() {
        when(advertisementRepository.findById(9999L)).thenReturn(Optional.empty());

        assertThrows(AdvertisementNotFoundException.class,
                () -> taskService.setAdvertisement(userDetail, TASK_ID, 9999L));
        verify(tasksRepository, never()).findByIdAndCustomer_Id(anyLong(), anyLong());
    }

    @Test
    @DisplayName("Тест setAdvertisement задача не найдена")
    public void testSetAdvertisementTaskNotFound() {
        Advertisement advertisement = advertisement(AD_ID);

        when(advertisementRepository.findById(AD_ID)).thenReturn(Optional.of(advertisement));
        when(tasksRepository.findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.setAdvertisement(userDetail, TASK_ID, AD_ID));
        verify(tasksRepository, never()).save(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест setAdvertisement задача уже занята")
    public void testSetAdvertisementTaskBusy() {
        Advertisement advertisement = advertisement(AD_ID);
        Tasks task = task(TASK_ID, TasksStatus.CREATED);
        task.setAdvertisement(advertisement(777L));

        when(advertisementRepository.findById(AD_ID)).thenReturn(Optional.of(advertisement));
        when(tasksRepository.findByIdAndCustomer_Id(TASK_ID, CUSTOMER_ID)).thenReturn(Optional.of(task));

        assertThrows(TaskBusyException.class,
                () -> taskService.setAdvertisement(userDetail, TASK_ID, AD_ID));
        verify(tasksRepository, never()).save(any(Tasks.class));
    }

    @Test
    @DisplayName("Тест deleteAdvertisement")
    public void testDeleteAdvertisementSuccess() {
        Tasks updated = task(TASK_ID, TasksStatus.CREATED);
        TaskDTO expected = dto(TASK_ID, TasksStatus.CREATED);

        when(tasksRepository.deleteAdvertisement(TASK_ID, CUSTOMER_ID, null))
                .thenReturn(List.of(updated));
        when(tasksRepository.updateTaskStatus(TASK_ID, CUSTOMER_ID, "CREATED"))
                .thenReturn(List.of(updated));
        when(taskMapper.getDTO(any(Tasks.class))).thenReturn(expected);

        TaskDTO result = taskService.deleteAdvertisement(userDetail, TASK_ID);

        assertNotNull(result);
        assertEquals(TASK_ID, result.getId());
        verify(tasksRepository).deleteAdvertisement(TASK_ID, CUSTOMER_ID, null);
        verify(tasksRepository).updateTaskStatus(TASK_ID, CUSTOMER_ID, "CREATED");
    }

    @Test
    @DisplayName("Тест deleteAdvertisement null")
    public void testDeleteAdvertisementNotFound() {
        when(tasksRepository.deleteAdvertisement(9999L, CUSTOMER_ID, null))
                .thenReturn(List.of());

        assertThrows(TaskNotFoundException.class,
                () -> taskService.deleteAdvertisement(userDetail, 9999L));
        verify(tasksRepository, never()).updateTaskStatus(anyLong(), anyLong(), anyString());
    }

    @Test
    @DisplayName("Тест deleteTask")
    public void testDeleteTask() {
        taskService.deleteTask(userDetail, TASK_ID);

        verify(tasksRepository).delete(TASK_ID, CUSTOMER_ID);
        verifyNoMoreInteractions(tasksRepository);
        verifyNoInteractions(taskMapper, imageService, advertisementRepository);
    }
}