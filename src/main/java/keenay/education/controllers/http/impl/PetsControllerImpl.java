package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.PetsController;
import keenay.education.dto.pets.PetsBodyDTO;
import keenay.education.dto.pets.PetsDTO;
import keenay.education.dto.pets.PetsPutBodyDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.PetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_customer')")
public class PetsControllerImpl implements PetsController {

    private final PetsService petsService;

    @PostMapping
    public PetsDTO createPets(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody PetsBodyDTO petsBodyDTO
    ) {
        return petsService.createPets(userDetail, petsBodyDTO);
    }

    @GetMapping
    public List<PetsDTO> getListPets(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return petsService.getListPets(userDetail);
    }

    @GetMapping("/{id}")
    public PetsDTO getPet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return petsService.getPet(userDetail, id);
    }

    @PutMapping("/{id}")
    public PetsDTO updatePet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody PetsPutBodyDTO petsBodyDTO
    ) {
        return petsService.updatePet(userDetail, id, petsBodyDTO);
    }

    @DeleteMapping("/{id}")
    public void deletePet(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        petsService.deletePet(userDetail, id);
    }
}