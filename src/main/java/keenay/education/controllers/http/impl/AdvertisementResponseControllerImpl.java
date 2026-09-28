package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.AdvertisementResponseController;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyStatusDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.AdvertisementResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdvertisementResponseControllerImpl implements AdvertisementResponseController {

    private final AdvertisementResponseService advertisementResponseService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public AdvertisementResponseDTO createAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody AdvertisementResponseBodyDTO advertisementBodyDTO
    ) {
        return advertisementResponseService.createAdvertisementResponse(userDetail, advertisementBodyDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public AdvertisementResponseDTO getAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return advertisementResponseService.getAdvertisementResponse(userDetail, id);
    }

    @GetMapping("/created/{id}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public List<AdvertisementResponseDTO> getResponses(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return advertisementResponseService.getResponses(userDetail, id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public List<AdvertisementResponseDTO> getAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return advertisementResponseService.getAdvertisementResponses(userDetail);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public void deleteAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        advertisementResponseService.deleteAdvertisementResponse(userDetail, id);
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public AdvertisementResponseDTO updateStatus(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody AdvertisementResponseBodyStatusDTO advertisementResponseBodyStatusDTO
    ) {
        return advertisementResponseService.updateStatus(userDetail, id, advertisementResponseBodyStatusDTO);
    }
}