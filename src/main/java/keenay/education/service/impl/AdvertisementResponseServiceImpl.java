package keenay.education.service.impl;

import keenay.education.dto.advertisement_response.AdvertisementResponseBodyDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyStatusDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.entity.AdvertisementResponse;
import keenay.education.exception.errors.AdvertisementNotFoundException;
import keenay.education.exception.errors.AdvertisementResponseNotFoundException;
import keenay.education.mapper.advertisement_response.AdvertisementResponseMapping;
import keenay.education.repository.jpa.AdvertisementRepository;
import keenay.education.repository.jpa.AdvertisementResponseRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.AdvertisementResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdvertisementResponseServiceImpl implements AdvertisementResponseService {
    private final AdvertisementResponseRepository advertisementResponseRepository;
    private final AdvertisementRepository advertisementRepository;
    private final AdvertisementResponseMapping advertisementResponseMapping;

    private AdvertisementResponse create(CustomUserDetail customUserDetail, Long advertisementId,
                                         AdvertisementResponseBodyDTO advertisementResponseBodyDTO) {
        AdvertisementResponse advertisementResponse = AdvertisementResponse.builder()
                .sellerId(customUserDetail.getSellerId())
                .price(advertisementResponseBodyDTO.getPrice())
                .advertisementId(advertisementId)
                .comment(advertisementResponseBodyDTO.getComment())
                .build();
        return advertisementResponseRepository.save(advertisementResponse);
    }

    @Override
    public AdvertisementResponseDTO createAdvertisementResponse(CustomUserDetail customUserDetail, AdvertisementResponseBodyDTO advertisementResponseBodyDTO) {
        return advertisementResponseMapping.getDTO(
                create(customUserDetail,
                        advertisementResponseBodyDTO.getAdvertisementId(),
                        advertisementResponseBodyDTO));
    }

    @Override
    public AdvertisementResponseDTO getAdvertisementResponse(CustomUserDetail customUserDetail, Long id) {
        AdvertisementResponse advertisementResponse = advertisementResponseRepository.findByIdAndSeller_Id(
                id, customUserDetail.getSellerId()
        ).orElseThrow(() -> new AdvertisementResponseNotFoundException("Advertisement response is not found."));
        return advertisementResponseMapping.getDTO(advertisementResponse);
    }

    @Override
    public List<AdvertisementResponseDTO> getAdvertisementResponses(CustomUserDetail customUserDetail) {
        return advertisementResponseRepository.findAllBySeller_Id(customUserDetail.getSellerId())
                .stream()
                .map(advertisementResponseMapping::getDTO)
                .toList();
    }

    @Override
    public List<AdvertisementResponseDTO> getResponses(CustomUserDetail customUserDetail, Long id) {
        return advertisementRepository.findByIdAndCustomer_Id(id, customUserDetail.getCustomerId())
                .orElseThrow(() -> new AdvertisementNotFoundException("Advertisement is not found."))
                .getResponses()
                .stream()
                .map(advertisementResponseMapping::getDTO)
                .toList();
    }

    @Override
    public AdvertisementResponseDTO updateStatus(CustomUserDetail customUserDetail, Long id, AdvertisementResponseBodyStatusDTO advertisementResponseBodyStatusDTO) {
        List<AdvertisementResponse> advertisementResponseList = advertisementResponseRepository.updateStatus(
                id, customUserDetail.getSellerId(), advertisementResponseBodyStatusDTO.getStatus().name()
        );
        if (advertisementResponseList.isEmpty()) {
            throw new AdvertisementResponseNotFoundException("Advertisement response is not found.");
        }
        return advertisementResponseMapping.getDTO(advertisementResponseList.get(0));
    }

    @Override
    public void deleteAdvertisementResponse(CustomUserDetail customUserDetail, Long id) {
        advertisementResponseRepository.delete(id, customUserDetail.getSellerId());
    }
}
