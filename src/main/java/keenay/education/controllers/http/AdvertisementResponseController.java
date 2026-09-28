package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseBodyStatusDTO;
import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Advertisement Response Endpoints")
@RequestMapping("/api/v1/advertisement/response")
public interface AdvertisementResponseController {
    AdvertisementResponseDTO createAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody AdvertisementResponseBodyDTO advertisementBodyDTO
    );

    AdvertisementResponseDTO getAdvertisement(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    List<AdvertisementResponseDTO> getResponses(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    List<AdvertisementResponseDTO> getAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail
    );

    void deleteAdvertisements(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    AdvertisementResponseDTO updateStatus(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id,
            @Valid @RequestBody AdvertisementResponseBodyStatusDTO advertisementResponseBodyStatusDTO
    );
}