package keenay.education.service.impl;

import keenay.education.dto.tasks.TaskBodyDTO;
import keenay.education.dto.tasks.TaskBodyStatusDTO;
import keenay.education.dto.tasks.TaskDTO;
import keenay.education.entity.Tasks;
import keenay.education.entity.status.TasksStatus;
import keenay.education.exception.errors.TaskBusyException;
import keenay.education.exception.errors.TaskNotFoundException;
import keenay.education.mapper.tasks.TaskMapper;
import keenay.education.repository.jpa.AdvertisementRepository;
import keenay.education.repository.jpa.TasksRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.TaskService;
import keenay.education.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;
    private final TasksRepository tasksRepository;
    private final ImageService imageService;
    private final AdvertisementRepository advertisementRepository;

    private Tasks createTaskWithPhoto(CustomUserDetail userDetail, TaskBodyDTO taskBodyDTO, MultipartFile photo) {
        Tasks task = Tasks.builder()
                .customerId(userDetail.getCustomerId())
                .title(taskBodyDTO.getTitle())
                .description(taskBodyDTO.getDescription())
                .photoUrl(!photo.isEmpty()?imageService.uploadPhoto(photo, userDetail):null)
                .build();
        return tasksRepository.save(task);
    }

    @Override
    public TaskDTO createTask(CustomUserDetail customUserDetail, TaskBodyDTO taskBodyDTO, MultipartFile multipartFile) {
        Tasks task = createTaskWithPhoto(customUserDetail, taskBodyDTO, multipartFile);
        return taskMapper.getDTO(task);
    }

    @Override
    public TaskDTO getTask(CustomUserDetail customUserDetail, Long id) {
        return taskMapper.getDTO(tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getCustomerId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found.")));
    }

    @Override
    public List<TaskDTO> getAvailTasks(CustomUserDetail customUserDetail) {
        return tasksRepository.findAllByCustomer_Id(customUserDetail.getCustomerId())
                .stream()
                .map(taskMapper::getDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getCreatedTasks(CustomUserDetail customUserDetail) {
        return tasksRepository.findAllByCustomer_IdAndStatus(customUserDetail.getCustomerId(),
                TasksStatus.CREATED).stream()
                .map(taskMapper::getDTO)
                .toList();
    }

    @Override
    public TaskDTO updateTask(CustomUserDetail customUserDetail, Long id, TaskBodyDTO taskBodyDTO) {
        List<Tasks> tasks = tasksRepository.updateTask(id, customUserDetail.getCustomerId(),
                taskBodyDTO.getTitle(), taskBodyDTO.getDescription());
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        return taskMapper.getDTO(tasks.get(0));
    }

    @Override
    @Transactional
    public TaskDTO updatePhotoTask(CustomUserDetail customUserDetail, Long id, MultipartFile photo) {
        Tasks pasted = tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getCustomerId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found."));
        String photoUrl = imageService.uploadPhoto(photo, customUserDetail);
        Tasks updated = tasksRepository.updateTaskPhoto(id, customUserDetail.getCustomerId(), photoUrl).get(0);
        if (!pasted.getPhotoUrl().isEmpty()) {
            imageService.deletePhoto(pasted.getPhotoUrl());
        }
        return taskMapper.getDTO(updated);
    }

    @Override
    public TaskDTO updateTaskStatus(CustomUserDetail customUserDetail, Long id, TaskBodyStatusDTO taskBodyStatusDTO) {
        List<Tasks> tasks = tasksRepository.updateTaskStatus(id, customUserDetail.getCustomerId(),
                taskBodyStatusDTO.getStatus().name());
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        return taskMapper.getDTO(tasks.get(0));
    }

    @Override
    public TaskDTO setAdvertisement(CustomUserDetail customUserDetail, Long id, Long advertisementId) {
        Tasks task = tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getCustomerId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found."));
        if (task.getAdvertisement() != null) {
            throw new TaskBusyException("Task is busy");
        }
        task.setAdvertisementId(advertisementId);
        this.updateTaskStatus(customUserDetail, id, new TaskBodyStatusDTO(TasksStatus.PROGRESS));
        return taskMapper.getDTO(tasksRepository.save(task));
    }

    @Override
    public TaskDTO deleteAdvertisement(CustomUserDetail customUserDetail, Long id) {
        List<Tasks> tasks = tasksRepository.deleteAdvertisement(id,
                customUserDetail.getCustomerId(), null);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        this.updateTaskStatus(customUserDetail, id, new TaskBodyStatusDTO(TasksStatus.CREATED));
        return taskMapper.getDTO(tasks.get(0));
    }


    @Override
    public void deleteTask(CustomUserDetail customUserDetail, Long id) {
        List<Tasks> tasks = tasksRepository.delete(id, customUserDetail.getCustomerId());
        for (Tasks task : tasks) {
            if (!task.getPhotoUrl().isEmpty()) {
                imageService.deletePhoto(task.getPhotoUrl());
            }
        }
    }
}
