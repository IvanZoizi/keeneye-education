package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import keenay.education.dto.advertisement.AdvertisementBodyDTO;
import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Advertisement Endpoints")
@RequestMapping("/api/v1/advertisement")
public interface AdvertisementController {
    AdvertisementDTO createAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody AdvertisementBodyDTO advertisementBodyDTO
    );

    List<AdvertisementDTO> getAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail
    );

    AdvertisementDTO getAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    void deleteAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    AdvertisementDTO addTask(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id,
            @PathVariable("taskId") Long taskId
    );

    AdvertisementDTO deleteTask(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id,
            @PathVariable("taskId") Long taskId
    );

    AdvertisementDTO setResponse(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id,
            @PathVariable("responseId") Long responseId
    );
}