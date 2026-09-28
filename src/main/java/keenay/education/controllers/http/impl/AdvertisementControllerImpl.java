package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.AdvertisementController;
import keenay.education.dto.advertisement.AdvertisementBodyDTO;
import keenay.education.dto.advertisement.AdvertisementBodyStatusDTO;
import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.AdvertisementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AdvertisementControllerImpl implements AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO createAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody AdvertisementBodyDTO advertisementBodyDTO
    ) {
        return advertisementService.createAdvertisement(userDetail, advertisementBodyDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO getAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return advertisementService.getAdvertisement(userDetail, id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public List<AdvertisementDTO> getAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return advertisementService.getAdvertisements(userDetail);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public void deleteAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        advertisementService.deleteAdvertisement(userDetail, id);
    }

    @PostMapping("/{id}/task/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO addTask(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @PathVariable Long taskId
    ) {
        return advertisementService.addTask(userDetail, id, taskId);
    }

    @DeleteMapping("/{id}/task/{taskId}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO deleteTask(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @PathVariable Long taskId
    ) {
        return advertisementService.deleteTask(userDetail, id, taskId);
    }

    @PutMapping("/status/{id}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO updateStatus(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody AdvertisementBodyStatusDTO advertisementBodyStatusDTO
    ) {
        return advertisementService.updateStatus(userDetail, id, advertisementBodyStatusDTO);
    }

    @PutMapping("/{id}/response/{responseId}")
    @PreAuthorize("hasAuthority('ROLE_customer')")
    public AdvertisementDTO setResponse(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @PathVariable Long responseId
    ) {
        return advertisementService.setResponse(userDetail, id, responseId);
    }

    @GetMapping("/by/skill")
    @PreAuthorize("hasAuthority('ROLE_seller')")
    public List<AdvertisementDTO> getAdvertisementBySkills(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return advertisementService.getAdvertisementBySkills(userDetail);
    }
}