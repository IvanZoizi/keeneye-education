package keenay.education.service;

import keenay.education.dto.advertisement.AdvertisementBodyDTO;
import keenay.education.dto.advertisement.AdvertisementBodyStatusDTO;
import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.entity.Advertisement;
import keenay.education.security.CustomUserDetail;

import java.util.List;

public interface AdvertisementService {
    AdvertisementDTO createAdvertisement(CustomUserDetail customUserDetail, AdvertisementBodyDTO advertisementBodyDTO);
    AdvertisementDTO getAdvertisement(CustomUserDetail customUserDetail, Long id);
    List<AdvertisementDTO> getAdvertisements(CustomUserDetail customUserDetail);
    void deleteAdvertisement(CustomUserDetail customUserDetail, Long id);
    AdvertisementDTO addTask(CustomUserDetail customUserDetail, Long id, Long taskId);
    AdvertisementDTO deleteTask(CustomUserDetail customUserDetail, Long id, Long taskId);
    AdvertisementDTO updateStatus(CustomUserDetail customUserDetail, Long Id,
                                  AdvertisementBodyStatusDTO advertisementBodyStatusDTO);
    AdvertisementDTO setResponse(CustomUserDetail customUserDetail, Long id, Long responseId);
    List<AdvertisementDTO> getAdvertisementBySkills(CustomUserDetail customUserDetail);
    AdvertisementDTO completeAdvertisement(CustomUserDetail customUserDetail, Long id);
    AdvertisementDTO canceledAdvertisement(CustomUserDetail customUserDetail, Long id);
}
