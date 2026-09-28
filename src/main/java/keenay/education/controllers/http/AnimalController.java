package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import keenay.education.dto.animal.AnimalBodyDTO;
import keenay.education.dto.animal.AnimalDTO;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Animal Endpoints")
@RequestMapping("/api/v1/animals")
public interface AnimalController {
    AnimalDTO createAnimal(@Valid AnimalBodyDTO animalBodyDTO);
    List<AnimalDTO> getAnimals();
    void deleteAnimal(@Valid AnimalBodyDTO animalBodyDTO);
}