package keenay.education.service.impl;

import keenay.education.dto.animal.AnimalBodyDTO;
import keenay.education.dto.animal.AnimalDTO;
import keenay.education.entity.Animals;
import keenay.education.mapper.animals.AnimalMapper;
import keenay.education.repository.jpa.AnimalsRepository;
import keenay.education.service.AnimalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnimalServiceImpl implements AnimalService {
    private final AnimalsRepository animalsRepository;
    private final AnimalMapper mapper;

    @Override
    public AnimalDTO createAnimal(AnimalBodyDTO animalDTO) {
        Animals animal = new Animals(animalDTO.getName());
        return mapper.getAnimal(animalsRepository.save(animal));
    }

    @Override
    public List<AnimalDTO> getAnimals() {
        return animalsRepository.findAll().stream()
                .map(mapper::getAnimal)
                .toList();
    }

    @Override
    public void deleteAnimal(AnimalBodyDTO animalDTO) {
        animalsRepository.deleteByName(animalDTO.getName());
    }
}
