package keenay.education.service.impl;

import keenay.education.dto.pets.PetsBodyDTO;
import keenay.education.dto.pets.PetsDTO;
import keenay.education.dto.pets.PetsPutBodyDTO;
import keenay.education.entity.Animals;
import keenay.education.entity.Customers;
import keenay.education.entity.Pets;
import keenay.education.entity.PetsProfile;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.exception.errors.AnimalIsNotSupported;
import keenay.education.exception.errors.EntityNotFoundException;
import keenay.education.exception.errors.PetsNotFoundException;
import keenay.education.mapper.pets.PetsMapper;
import keenay.education.repository.AnimalsRepository;
import keenay.education.repository.PetsProfileRepository;
import keenay.education.repository.PetsRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.PetsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PetsServiceImpl implements PetsService {

    private final PetsRepository petsRepository;
    private final PetsProfileRepository petsProfileRepository;
    private final AnimalsRepository animalsRepository;
    private final PetsMapper mapperService;

    private Pets createPets(PetsBodyDTO petsBodyDTO, Customers customer, Animals animal) {
        Pets pets = Pets.builder()
                .animal(animal)
                .customer(customer)
                .name(petsBodyDTO.getNamePet())
                .build();
        return petsRepository.save(pets);
    }

    private PetsProfile createPetsProfile(PetsBodyDTO petsBodyDTO, Pets pet) {
        PetsProfile petsProfile = PetsProfile.builder()
                .pet(pet)
                .breed(petsBodyDTO.getBreed())
                .features(petsBodyDTO.getFeatures())
                .vaccinations(petsBodyDTO.getVaccinations())
                .build();
        return petsProfileRepository.save(petsProfile);
    }

    @Override
    @Transactional
    public PetsDTO createPets(CustomUserDetail userDetail, PetsBodyDTO petsBodyDTO) {
        Customers customers = userDetail.getUser().getCustomer();
        Animals animal = animalsRepository.findByName(petsBodyDTO.getNameAnimal())
                .orElseThrow(() -> new AnimalIsNotSupported("This animal is not handled in our service."));
        Pets pets = createPets(petsBodyDTO, customers, animal);
        pets.setPetsProfile(createPetsProfile(petsBodyDTO, pets));
        return mapperService.getPets(pets);
    }

    @Override
    public List<PetsDTO> getListPets(CustomUserDetail userDetail) {
        return petsRepository.findByCustomer_Id(userDetail.getUser().getId()).stream()
                .map(mapperService::getPets)
                .toList();
    }

    @Override
    public PetsDTO getPet(CustomUserDetail userDetail, Long id) {
        Pets pet = petsRepository.findByIdAndUserId(id, userDetail.getUser().getId())
                .orElseThrow(() -> new AccessDeniedException("You cannot obtain information about this pet."));
        return mapperService.getPets(pet);
    }

    @Override
    public PetsDTO updatePet(CustomUserDetail userDetail, Long id, PetsPutBodyDTO petsBodyDTO) {
        Pets pet = petsRepository.findByIdAndUserId(id, userDetail.getUser().getId())
                .orElseThrow(() -> new AccessDeniedException("You cannot obtain information about this pet."));
        List<PetsProfile> petsProfile = petsProfileRepository.update(
                id,
                userDetail.getUser().getCustomer().getId(),
                petsBodyDTO.getBreed(),
                petsBodyDTO.getFeatures(),
                petsBodyDTO.getVaccinations()
        );
        if (petsProfile.isEmpty()) {
            throw new PetsNotFoundException("The pet was not found.");
        }
        pet.setPetsProfile(petsProfile.get(0));
        return mapperService.getPets(pet);
    }

    @Override
    public void deletePet(CustomUserDetail userDetail, Long id) {
        petsRepository.deleteByIdAndCustomer(id, userDetail.getUser().getId())
                .orElseThrow(() -> new AccessDeniedException("This pet does not belong to you."));
    }
}
