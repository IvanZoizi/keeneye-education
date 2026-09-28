package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.AnimalController;
import keenay.education.dto.animal.AnimalBodyDTO;
import keenay.education.dto.animal.AnimalDTO;
import keenay.education.service.AnimalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_admin')")
public class AnimalControllerImpl implements AnimalController {

    private final AnimalService animalService;

    @PostMapping("/animal")
    public AnimalDTO createAnimal(@Valid @RequestBody AnimalBodyDTO animalBodyDTO) {
        return animalService.createAnimal(animalBodyDTO);
    }

    @GetMapping("/animal")
    @PreAuthorize("permitAll()")
    public List<AnimalDTO> getAnimals() {
        return animalService.getAnimals();
    }

    @DeleteMapping("/animal")
    public void deleteAnimal(@Valid @RequestBody AnimalBodyDTO animalBodyDTO) {
        animalService.deleteAnimal(animalBodyDTO);
    }
}