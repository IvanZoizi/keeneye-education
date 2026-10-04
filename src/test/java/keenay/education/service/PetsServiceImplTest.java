package keenay.education.service;

import keenay.education.dto.pets.PetsBodyDTO;
import keenay.education.dto.pets.PetsDTO;
import keenay.education.dto.pets.PetsPutBodyDTO;
import keenay.education.entity.Animals;
import keenay.education.entity.Customers;
import keenay.education.entity.Pets;
import keenay.education.entity.PetsProfile;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.exception.errors.AnimalIsNotSupported;
import keenay.education.exception.errors.PetsNotFoundException;
import keenay.education.mapper.pets.PetsMapper;
import keenay.education.repository.jpa.AnimalsRepository;
import keenay.education.repository.jpa.PetsProfileRepository;
import keenay.education.repository.jpa.PetsRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.PetsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PetsServiceImplTest {

    private static final Long USER_ID = 1L;
    private static final Long CUSTOMER_ID = 2L;
    private static final Long PET_ID = 10L;

    @Mock
    private PetsRepository petsRepository;

    @Mock
    private PetsProfileRepository petsProfileRepository;

    @Mock
    private AnimalsRepository animalsRepository;

    @Mock
    private PetsMapper mapperService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @InjectMocks
    private PetsServiceImpl petsService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getUserId()).thenReturn(USER_ID);
        lenient().when(userDetail.getCustomerId()).thenReturn(CUSTOMER_ID);
    }

    private Animals animal(String name) {
        Animals animal = new Animals();
        animal.setName(name);
        return animal;
    }

    private Pets pet(Long id) {
        Pets pet = new Pets();
        pet.setId(id);
        pet.setName("Buddy");
        return pet;
    }

    private PetsProfile profile(Long id) {
        PetsProfile profile = new PetsProfile();
        profile.setId(id);
        profile.setBreed("Labrador");
        profile.setFeatures("Friendly");
        profile.setVaccinations("Yes");
        return profile;
    }

    private PetsDTO dto(Long id) {
        PetsDTO dto = new PetsDTO();
        dto.setId(id);
        dto.setNameAnimal("Dog");
        dto.setNamePet("Buddy");
        dto.setBreed("Labrador");
        dto.setFeatures("Friendly");
        dto.setVaccinations("Yes");
        return dto;
    }

    private PetsBodyDTO body() {
        PetsBodyDTO body = new PetsBodyDTO();
        body.setNameAnimal("Dog");
        body.setNamePet("Buddy");
        body.setBreed("Labrador");
        body.setFeatures("Friendly");
        body.setVaccinations("Yes");
        return body;
    }

    private PetsPutBodyDTO putBody() {
        PetsPutBodyDTO body = new PetsPutBodyDTO();
        body.setBreed("Labrador");
        body.setFeatures("Friendly");
        body.setVaccinations("Yes");
        return body;
    }

    @Test
    @DisplayName("Тест createPets")
    public void testCreatePetsSuccess() {
        Animals animal = animal("Dog");
        Customers customer = new Customers();
        customer.setId(CUSTOMER_ID);
        PetsDTO expected = dto(PET_ID);

        when(animalsRepository.findByName("Dog")).thenReturn(Optional.of(animal));
        when(petsRepository.save(any(Pets.class))).thenAnswer(invocation -> {
            Pets p = invocation.getArgument(0);
            assertEquals(animal, p.getAnimal());
            assertEquals(customer, p.getCustomer());
            assertEquals("Buddy", p.getName());
            p.setId(PET_ID);
            return p;
        });
        when(petsProfileRepository.save(any(PetsProfile.class))).thenAnswer(invocation -> {
            PetsProfile pr = invocation.getArgument(0);
            assertEquals("Labrador", pr.getBreed());
            assertEquals("Friendly", pr.getFeatures());
            assertEquals("Yes", pr.getVaccinations());
            assertNotNull(pr.getPet());
            return pr;
        });
        when(mapperService.getPets(any(Pets.class))).thenReturn(expected);

        PetsDTO result = petsService.createPets(userDetail, body());

        assertNotNull(result);
        assertEquals(PET_ID, result.getId());
        verify(animalsRepository).findByName("Dog");
        verify(petsRepository).save(any(Pets.class));
        verify(petsProfileRepository).save(any(PetsProfile.class));
        verify(mapperService).getPets(any(Pets.class));
    }

    @Test
    @DisplayName("Тест createPets животное не поддерживается")
    public void testCreatePetsAnimalNotSupported() {
        when(animalsRepository.findByName("Dog")).thenReturn(Optional.empty());

        assertThrows(AnimalIsNotSupported.class,
                () -> petsService.createPets(userDetail, body()));
        verify(petsRepository, never()).save(any(Pets.class));
        verifyNoInteractions(petsProfileRepository, mapperService);
    }

    @Test
    @DisplayName("Тест getListPets")
    public void testGetListPetsSuccess() {
        Pets first = pet(1L);
        Pets second = pet(2L);

        when(petsRepository.findByCustomer_Id(USER_ID)).thenReturn(List.of(first, second));
        when(mapperService.getPets(first)).thenReturn(dto(1L));
        when(mapperService.getPets(second)).thenReturn(dto(2L));

        List<PetsDTO> result = petsService.getListPets(userDetail);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(petsRepository).findByCustomer_Id(USER_ID);
        verify(mapperService, times(2)).getPets(any(Pets.class));
    }

    @Test
    @DisplayName("Тест getListPets пустой список")
    public void testGetListPetsEmpty() {
        when(petsRepository.findByCustomer_Id(USER_ID)).thenReturn(List.of());

        List<PetsDTO> result = petsService.getListPets(userDetail);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(mapperService);
    }

    @Test
    @DisplayName("Тест getPet")
    public void testGetPetSuccess() {
        Pets pet = pet(PET_ID);
        PetsDTO expected = dto(PET_ID);

        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.of(pet));
        when(mapperService.getPets(pet)).thenReturn(expected);

        PetsDTO result = petsService.getPet(userDetail, PET_ID);

        assertNotNull(result);
        assertEquals(PET_ID, result.getId());
        verify(petsRepository).findByIdAndUserId(PET_ID, USER_ID);
        verify(mapperService).getPets(pet);
    }

    @Test
    @DisplayName("Тест getPet null")
    public void testGetPetNotFound() {
        when(petsRepository.findByIdAndUserId(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> petsService.getPet(userDetail, 9999L));
        verify(mapperService, never()).getPets(any(Pets.class));
    }

    @Test
    @DisplayName("Тест updatePet")
    public void testUpdatePetSuccess() {
        Pets pet = pet(PET_ID);
        PetsProfile profile = profile(1L);
        PetsDTO expected = dto(PET_ID);

        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.of(pet));
        when(petsProfileRepository.update(PET_ID, CUSTOMER_ID, "Labrador", "Friendly", "Yes"))
                .thenReturn(List.of(profile));
        when(mapperService.getPets(pet)).thenReturn(expected);

        PetsDTO result = petsService.updatePet(userDetail, PET_ID, putBody());

        assertNotNull(result);
        assertEquals(PET_ID, result.getId());
        assertEquals(profile, pet.getPetsProfile());
        verify(petsProfileRepository).update(PET_ID, CUSTOMER_ID, "Labrador", "Friendly", "Yes");
        verify(mapperService).getPets(pet);
    }

    @Test
    @DisplayName("Тест updatePet null")
    public void testUpdatePetNotFound() {
        when(petsRepository.findByIdAndUserId(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> petsService.updatePet(userDetail, 9999L, putBody()));
        verify(petsProfileRepository, never()).update(anyLong(), anyLong(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("Тест updatePet профиль не найден")
    public void testUpdatePetProfileNotFound() {
        Pets pet = pet(PET_ID);

        when(petsRepository.findByIdAndUserId(PET_ID, USER_ID)).thenReturn(Optional.of(pet));
        when(petsProfileRepository.update(PET_ID, CUSTOMER_ID, "Labrador", "Friendly", "Yes"))
                .thenReturn(List.of());

        assertThrows(PetsNotFoundException.class,
                () -> petsService.updatePet(userDetail, PET_ID, putBody()));
        verify(mapperService, never()).getPets(any(Pets.class));
    }

    @Test
    @DisplayName("Тест deletePet")
    public void testDeletePetSuccess() {
        Pets pet = pet(PET_ID);
        when(petsRepository.deleteByIdAndCustomer(PET_ID, USER_ID)).thenReturn(Optional.of(pet));

        petsService.deletePet(userDetail, PET_ID);

        verify(petsRepository).deleteByIdAndCustomer(PET_ID, USER_ID);
        verifyNoMoreInteractions(petsRepository);
        verifyNoInteractions(mapperService, petsProfileRepository, animalsRepository);
    }

    @Test
    @DisplayName("Тест deletePet null")
    public void testDeletePetNotFound() {
        when(petsRepository.deleteByIdAndCustomer(9999L, USER_ID)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class,
                () -> petsService.deletePet(userDetail, 9999L));
    }
}