package keenay.education.service;

import keenay.education.dto.skills.SkillsBodyDTO;
import keenay.education.dto.skills.SkillsDTO;
import keenay.education.dto.skills.SkillsPutBodyDTO;
import keenay.education.entity.Animals;
import keenay.education.entity.Sellers;
import keenay.education.entity.Skills;
import keenay.education.exception.errors.AnimalIsNotSupported;
import keenay.education.exception.errors.SkillNotFoundException;
import keenay.education.exception.errors.SkillsNotFoundException;
import keenay.education.mapper.skills.SkillMapper;
import keenay.education.repository.jpa.AnimalsRepository;
import keenay.education.repository.jpa.SkillsRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.impl.SkillsServiceImpl;
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
public class SkillsServiceImplTest {

    private static final Long SELLER_ID = 3L;
    private static final Long SKILL_ID = 10L;

    @Mock
    private SkillsRepository skillsRepository;

    @Mock
    private AnimalsRepository animalsRepository;

    @Mock
    private SkillMapper mapperService;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private CustomUserDetail userDetail;

    @InjectMocks
    private SkillsServiceImpl skillsService;

    @BeforeEach
    public void setUp() {
        lenient().when(userDetail.getUser().getSeller().getId()).thenReturn(SELLER_ID);
    }

    private Animals animal(String name) {
        Animals animal = new Animals();
        animal.setName(name);
        return animal;
    }

    private Skills skill(Long id) {
        Skills skill = new Skills();
        skill.setId(id);
        skill.setTitle("Title");
        skill.setDescription("Description");
        return skill;
    }

    private SkillsDTO dto(Long id) {
        SkillsDTO dto = new SkillsDTO();
        dto.setId(id);
        dto.setTitle("Title");
        dto.setDescription("Description");
        dto.setAnimal("Dog");
        return dto;
    }

    private SkillsBodyDTO body() {
        SkillsBodyDTO body = new SkillsBodyDTO();
        body.setTitle("Title");
        body.setDescription("Description");
        body.setAnimal("Dog");
        return body;
    }

    private SkillsPutBodyDTO putBody() {
        SkillsPutBodyDTO body = new SkillsPutBodyDTO();
        body.setTitle("Title");
        body.setDescription("Description");
        return body;
    }

    @Test
    @DisplayName("Тест createSkillForUser")
    public void testCreateSkillForUserSuccess() {
        Animals animal = animal("Dog");
        Sellers seller = new Sellers();
        seller.setId(SELLER_ID);
        SkillsDTO expected = dto(SKILL_ID);

        when(userDetail.getUser().getSeller()).thenReturn(seller);
        when(animalsRepository.findByName("Dog")).thenReturn(Optional.of(animal));
        when(skillsRepository.save(any(Skills.class))).thenAnswer(invocation -> {
            Skills s = invocation.getArgument(0);
            assertEquals("Title", s.getTitle());
            assertEquals("Description", s.getDescription());
            assertEquals(animal, s.getAnimal());
            assertEquals(seller, s.getSeller());
            s.setId(SKILL_ID);
            return s;
        });
        when(mapperService.getSkill(any(Skills.class))).thenReturn(expected);

        SkillsDTO result = skillsService.createSkillForUser(userDetail, body());

        assertNotNull(result);
        assertEquals(SKILL_ID, result.getId());
        verify(animalsRepository).findByName("Dog");
        verify(skillsRepository).save(any(Skills.class));
        verify(mapperService).getSkill(any(Skills.class));
    }

    @Test
    @DisplayName("Тест createSkillForUser животное не поддерживается")
    public void testCreateSkillForUserAnimalNotSupported() {
        when(animalsRepository.findByName("Dog")).thenReturn(Optional.empty());

        assertThrows(AnimalIsNotSupported.class,
                () -> skillsService.createSkillForUser(userDetail, body()));
        verify(skillsRepository, never()).save(any(Skills.class));
        verifyNoInteractions(mapperService);
    }

    @Test
    @DisplayName("Тест getSkills")
    public void testGetSkillsSuccess() {
        Skills first = skill(1L);
        Skills second = skill(2L);

        when(skillsRepository.findAllBySeller_Id(SELLER_ID)).thenReturn(List.of(first, second));
        when(mapperService.getSkill(first)).thenReturn(dto(1L));
        when(mapperService.getSkill(second)).thenReturn(dto(2L));

        List<SkillsDTO> result = skillsService.getSkills(userDetail);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(skillsRepository).findAllBySeller_Id(SELLER_ID);
        verify(mapperService, times(2)).getSkill(any(Skills.class));
    }

    @Test
    @DisplayName("Тест getSkills пустой список")
    public void testGetSkillsEmpty() {
        when(skillsRepository.findAllBySeller_Id(SELLER_ID)).thenReturn(List.of());

        List<SkillsDTO> result = skillsService.getSkills(userDetail);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(mapperService);
    }

    @Test
    @DisplayName("Тест getSkill")
    public void testGetSkillSuccess() {
        Skills skill = skill(SKILL_ID);
        SkillsDTO expected = dto(SKILL_ID);

        when(skillsRepository.findAllByIdAndSeller_Id(SKILL_ID, SELLER_ID)).thenReturn(Optional.of(skill));
        when(mapperService.getSkill(skill)).thenReturn(expected);

        SkillsDTO result = skillsService.getSkill(userDetail, SKILL_ID);

        assertNotNull(result);
        assertEquals(SKILL_ID, result.getId());
        verify(skillsRepository).findAllByIdAndSeller_Id(SKILL_ID, SELLER_ID);
        verify(mapperService).getSkill(skill);
    }

    @Test
    @DisplayName("Тест getSkill null")
    public void testGetSkillNotFound() {
        when(skillsRepository.findAllByIdAndSeller_Id(9999L, SELLER_ID)).thenReturn(Optional.empty());

        assertThrows(SkillNotFoundException.class,
                () -> skillsService.getSkill(userDetail, 9999L));
        verify(mapperService, never()).getSkill(any(Skills.class));
    }

    @Test
    @DisplayName("Тест updateSkill")
    public void testUpdateSkillSuccess() {
        Skills skill = skill(SKILL_ID);
        SkillsDTO expected = dto(SKILL_ID);

        when(skillsRepository.update(SKILL_ID, SELLER_ID, "Title", "Description"))
                .thenReturn(List.of(skill));
        when(skillsRepository.save(skill)).thenReturn(skill);
        when(mapperService.getSkill(skill)).thenReturn(expected);

        SkillsDTO result = skillsService.updateSkill(userDetail, SKILL_ID, putBody());

        assertNotNull(result);
        assertEquals(SKILL_ID, result.getId());
        verify(skillsRepository).update(SKILL_ID, SELLER_ID, "Title", "Description");
        verify(skillsRepository).save(skill);
        verify(mapperService).getSkill(skill);
    }

    @Test
    @DisplayName("Тест updateSkill null")
    public void testUpdateSkillNotFound() {
        when(skillsRepository.update(9999L, SELLER_ID, "Title", "Description"))
                .thenReturn(List.of());

        assertThrows(SkillsNotFoundException.class,
                () -> skillsService.updateSkill(userDetail, 9999L, putBody()));
        verify(skillsRepository, never()).save(any(Skills.class));
        verify(mapperService, never()).getSkill(any(Skills.class));
    }

    @Test
    @DisplayName("Тест deleteSkill")
    public void testDeleteSkill() {
        skillsService.deleteSkill(userDetail, SKILL_ID);

        verify(skillsRepository).deleteAndReturning(SKILL_ID, SELLER_ID);
        verifyNoMoreInteractions(skillsRepository);
        verifyNoInteractions(mapperService, animalsRepository);
    }
}