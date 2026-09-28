package keenay.education.service.impl;

import keenay.education.dto.advertisement.AdvertisementBodyDTO;
import keenay.education.dto.advertisement.AdvertisementBodyStatusDTO;
import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyStatusDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.entity.*;
import keenay.education.entity.status.AdvertisementResponseStatus;
import keenay.education.entity.status.AdvertisementStatus;
import keenay.education.exception.errors.*;
import keenay.education.mapper.advertisement.AdvertisementMapper;
import keenay.education.repository.AdvertisementRepository;
import keenay.education.repository.AdvertisementResponseRepository;
import keenay.education.repository.PetsRepository;
import keenay.education.repository.TasksRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.AdvertisementResponseService;
import keenay.education.service.AdvertisementService;
import keenay.education.service.PetsService;
import keenay.education.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementServiceImpl implements AdvertisementService {

    private final PetsRepository petsRepository;
    private final TaskService taskService;
    private final TasksRepository tasksRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementMapper advertisementMapper;
    private final AdvertisementResponseRepository advertisementResponseRepository;
    private final AdvertisementResponseService advertisementResponseService;

    private Advertisement createAdvertisementWithEntity(Customers customer, Pets pet,
                                                        AdvertisementBodyDTO advertisementBodyDTO) {
        Advertisement advertisement = Advertisement.builder()
                .pet(pet)
                .customer(customer)
                .budget(advertisementBodyDTO.getBudget())
                .build();
        return advertisementRepository.save(advertisement);
    }

    @Override
    @Transactional
    public AdvertisementDTO createAdvertisement(CustomUserDetail customUserDetail, AdvertisementBodyDTO advertisementBodyDTO) {
        Pets pet = petsRepository.findByIdAndUserId(advertisementBodyDTO.getPetId(),
                        customUserDetail.getUser().getId())
                .orElseThrow(() -> new PetsNotFoundException("This pet is not found."));
        Advertisement advertisement = createAdvertisementWithEntity(customUserDetail.getUser().getCustomer(),
                pet, advertisementBodyDTO);
        for (Long taskId : advertisementBodyDTO.getListTasksId()) {
            taskService.setAdvertisement(customUserDetail, taskId, advertisement.getId());
        }
        return advertisementMapper.getDTO(advertisement);

    }

    @Override
    public AdvertisementDTO getAdvertisement(CustomUserDetail customUserDetail, Long id) {
        return advertisementMapper.getDTO(
                advertisementRepository.findByIdAndCustomer_Id(id,
                                customUserDetail.getUser().getCustomer().getId())
                        .orElseThrow(() -> new AdvertisementNotFoundException("Advertisement is not found.")));
    }

    @Override
    public List<AdvertisementDTO> getAdvertisements(CustomUserDetail customUserDetail) {
        return advertisementRepository.findAllByCustomer_Id(customUserDetail.getUser().getCustomer().getId())
                .stream()
                .map(advertisementMapper::getDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteAdvertisement(CustomUserDetail customUserDetail, Long id) {
        advertisementRepository.delete(id, customUserDetail.getUser().getCustomer().getId());
    }

    @Override
    public AdvertisementDTO addTask(CustomUserDetail customUserDetail, Long id, Long taskId) {
        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task is not found."));
        if (task.getAdvertisement() != null && task.getAdvertisement().getId().equals(id)) {
            throw new TaskBusyException("Task is busy");
        }
        taskService.setAdvertisement(customUserDetail, taskId, id);
        return getAdvertisement(customUserDetail, id);
    }

    @Override
    public AdvertisementDTO deleteTask(CustomUserDetail customUserDetail, Long id, Long taskId) {
        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task is not found."));
        if (!task.getAdvertisement().getId().equals(id)) {
            throw new TaskBusyException("Task is not busy");
        }
        taskService.deleteTask(customUserDetail, taskId);
        return getAdvertisement(customUserDetail, id);
    }

    @Override
    public AdvertisementDTO updateStatus(CustomUserDetail customUserDetail, Long id, AdvertisementBodyStatusDTO advertisementBodyStatusDTO) {
        List<Advertisement> advertisements = advertisementRepository.updateStatus(
                id, customUserDetail.getUser().getCustomer().getId(), advertisementBodyStatusDTO.getStatus().name());
        if (advertisements.isEmpty()) {
            throw new AdvertisementNotFoundException("Advertisement is not found.");
        }
        return advertisementMapper.getDTO(advertisements.get(0));
    }

    @Override
    @Transactional
    public AdvertisementDTO setResponse(CustomUserDetail customUserDetail, Long id, Long responseId) {
        AdvertisementResponse advertisementResponse = advertisementResponseRepository.findByIdAndSeller_Id(
                responseId, customUserDetail.getUser().getSeller().getId()
        ).orElseThrow(() -> new AdvertisementResponseNotFoundException("Advertisement response is not found."));
        System.out.println(advertisementResponse.getStatus());
        if (advertisementResponse.getStatus() != AdvertisementResponseStatus.CREATED) {
            throw new AdvertisementResponseBusyException("Advertisement response is busy.");
        }
        advertisementResponseService.updateStatus(
                customUserDetail, responseId,
                new AdvertisementResponseBodyStatusDTO(AdvertisementResponseStatus.SELECTED));
        Advertisement advertisement = advertisementRepository.findByIdAndCustomer_Id(id,
                        customUserDetail.getUser().getCustomer().getId())
                .orElseThrow(() -> new AdvertisementNotFoundException("Advertisement is not found."));
        for (AdvertisementResponse response : advertisement.getResponses()) {
            advertisementResponseService.updateStatus(
                    customUserDetail, response.getId(),
                    new AdvertisementResponseBodyStatusDTO(AdvertisementResponseStatus.REJECTED));
        }
        return this.updateStatus(customUserDetail, id, new AdvertisementBodyStatusDTO(AdvertisementStatus.PROGRESS));
    }

    @Override
    public List<AdvertisementDTO> getAdvertisementBySkills(CustomUserDetail customUserDetail) {
        return advertisementRepository.findAdvertisementBySkills(customUserDetail.getUser().getSeller().getId()).stream()
                .map(advertisementMapper::getDTO)
                .toList();
    }
}
