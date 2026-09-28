package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import keenay.education.dto.pets.PetsBodyDTO;
import keenay.education.dto.pets.PetsDTO;
import keenay.education.dto.pets.PetsPutBodyDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Pets Endpoints")
@RequestMapping("/api/v1/pets")
public interface PetsController {
    PetsDTO createPets(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody PetsBodyDTO petsBodyDTO
    );

    List<PetsDTO> getListPets(
            @AuthenticationPrincipal CustomUserDetail userDetail
    );

    PetsDTO getPet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );

    PetsDTO updatePet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id,
            @Valid @RequestBody PetsPutBodyDTO petsBodyDTO
    );

    void deletePet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable("id") Long id
    );
}