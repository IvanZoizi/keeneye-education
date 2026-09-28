package keenay.education.service.impl;

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
import keenay.education.repository.AdvertisementRepository;
import keenay.education.repository.TasksRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.TaskService;
import keenay.education.service.image.ImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.config.Task;
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
                .customer(userDetail.getUser().getCustomer())
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
        return taskMapper.getDTO(tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getUser().getCustomer().getId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found.")));
    }

    @Override
    public List<TaskDTO> getAvailTasks(CustomUserDetail customUserDetail) {
        return tasksRepository.findAllByCustomer_Id(customUserDetail.getUser().getCustomer().getId())
                .stream()
                .map(taskMapper::getDTO)
                .toList();
    }

    @Override
    public List<TaskDTO> getCreatedTasks(CustomUserDetail customUserDetail) {
        return tasksRepository.findAllByCustomer_IdAndStatus(customUserDetail.getUser().getCustomer().getId(),
                TasksStatus.CREATED).stream()
                .map(taskMapper::getDTO)
                .toList();
    }

    @Override
    public TaskDTO updateTask(CustomUserDetail customUserDetail, Long id, TaskBodyDTO taskBodyDTO) {
        List<Tasks> tasks = tasksRepository.updateTask(id, customUserDetail.getUser().getCustomer().getId(),
                taskBodyDTO.getTitle(), taskBodyDTO.getDescription());
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        return taskMapper.getDTO(tasks.get(0));
    }

    @Override
    @Transactional
    public TaskDTO updatePhotoTask(CustomUserDetail customUserDetail, Long id, MultipartFile photo) {
        Tasks pasted = tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getUser().getCustomer().getId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found."));

        String photoUrl = imageService.uploadPhoto(photo, customUserDetail);
        Tasks updated = tasksRepository.updateTaskPhoto(id, customUserDetail.getUser().getCustomer().getId(),
                photoUrl).get(0);
        if (!pasted.getPhotoUrl().isEmpty()) {
            imageService.deletePhoto(pasted.getPhotoUrl());
        }
        return taskMapper.getDTO(updated);
    }

    @Override
    public TaskDTO updateTaskStatus(CustomUserDetail customUserDetail, Long id, TaskBodyStatusDTO taskBodyStatusDTO) {
        List<Tasks> tasks = tasksRepository.updateTaskStatus(id, customUserDetail.getUser().getCustomer().getId(),
                taskBodyStatusDTO.getStatus().name());
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        return taskMapper.getDTO(tasks.get(0));
    }

    @Override
    public TaskDTO setAdvertisement(CustomUserDetail customUserDetail, Long id, Long advertisementId) {
        Advertisement advertisement = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new AdvertisementNotFoundException("This advertisement not found."));
        Tasks task = tasksRepository.findByIdAndCustomer_Id(id, customUserDetail.getUser().getCustomer().getId())
                .orElseThrow(() -> new TaskNotFoundException("This task is not found."));
        if (task.getAdvertisement() != null) {
            throw new TaskBusyException("Task is busy");
        }
        task.setAdvertisement(advertisement);
        this.updateTaskStatus(customUserDetail, id, new TaskBodyStatusDTO(TasksStatus.PROGRESS));
        return taskMapper.getDTO(tasksRepository.save(task));
    }

    @Override
    public TaskDTO deleteAdvertisement(CustomUserDetail customUserDetail, Long id) {
        List<Tasks> tasks = tasksRepository.deleteAdvertisement(id,
                customUserDetail.getUser().getCustomer().getId(), null);
        if (tasks.isEmpty()) {
            throw new TaskNotFoundException("This task is not found.");
        }
        this.updateTaskStatus(customUserDetail, id, new TaskBodyStatusDTO(TasksStatus.CREATED));
        return taskMapper.getDTO(tasks.get(0));
    }


    @Override
    public void deleteTask(CustomUserDetail customUserDetail, Long id) {
        List<Tasks> tasks = tasksRepository.delete(id, customUserDetail.getUser().getCustomer().getId());
        for (Tasks task : tasks) {
            if (!task.getPhotoUrl().isEmpty()) {
                imageService.deletePhoto(task.getPhotoUrl());
            }
        }
    }
}
