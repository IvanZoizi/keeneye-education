package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.TaskController;
import keenay.education.dto.tasks.TaskBodyDTO;
import keenay.education.dto.tasks.TaskBodyStatusDTO;
import keenay.education.dto.tasks.TaskDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_customer')")
public class TasksControllerImpl implements TaskController {

    private final TaskService taskService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TaskDTO createTask(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @Valid @ModelAttribute TaskBodyDTO taskBodyDTO,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        return taskService.createTask(customUserDetail, taskBodyDTO, file);
    }

    @GetMapping("/{id}")
    public TaskDTO getTask(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id
    ) {
        return taskService.getTask(customUserDetail, id);
    }

    @GetMapping
    public List<TaskDTO> getAvailTasks(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return taskService.getAvailTasks(customUserDetail);
    }

    @GetMapping("/created")
    public List<TaskDTO> getCreatedTasks(@AuthenticationPrincipal CustomUserDetail customUserDetail) {
        return taskService.getCreatedTasks(customUserDetail);
    }

    @PutMapping("/{id}")
    public TaskDTO updateTask(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id,
            @Valid @RequestBody TaskBodyDTO taskBodyDTO
    ) {
        return taskService.updateTask(customUserDetail, id, taskBodyDTO);
    }

    @PutMapping(value = "/photo/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public TaskDTO updatePhotoTask(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id,
            @RequestParam("file") MultipartFile photo
    ) {
        return taskService.updatePhotoTask(customUserDetail, id, photo);
    }

    @PutMapping("/status/{id}")
    public TaskDTO updateTaskStatus(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id,
            @Valid @RequestBody TaskBodyStatusDTO taskBodyStatusDTO
    ) {
        return taskService.updateTaskStatus(customUserDetail, id, taskBodyStatusDTO);
    }

    @PostMapping("/{id}/advertisement/{advertisementId}")
    public TaskDTO setAdvertisement(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id,
            @PathVariable Long advertisementId
    ) {
        return taskService.setAdvertisement(customUserDetail, id, advertisementId);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(
            @AuthenticationPrincipal CustomUserDetail customUserDetail,
            @PathVariable Long id
    ) {
        taskService.deleteTask(customUserDetail, id);
    }
}