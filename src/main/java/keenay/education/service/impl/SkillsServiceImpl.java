package keenay.education.service.impl;

import keenay.education.dto.skills.SkillsBodyDTO;
import keenay.education.dto.skills.SkillsDTO;
import keenay.education.dto.skills.SkillsPutBodyDTO;
import keenay.education.entity.Animals;
import keenay.education.entity.Skills;
import keenay.education.exception.errors.*;
import keenay.education.mapper.skills.SkillMapper;
import keenay.education.repository.jpa.AnimalsRepository;
import keenay.education.repository.jpa.SkillsRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SkillsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SkillsServiceImpl implements SkillsService {

    private final SkillsRepository skillsRepository;
    private final AnimalsRepository animalsRepository;
    private final SkillMapper mapperService;

    @Override
    public SkillsDTO createSkillForUser(CustomUserDetail userDetail, SkillsBodyDTO skillsBodyDTO) {
        Animals animal = animalsRepository.findByName(skillsBodyDTO.getAnimal())
                .orElseThrow(() -> new AnimalIsNotSupported("This animal is not handled in our service."));
        Skills skill = Skills.builder()
                .title(skillsBodyDTO.getTitle())
                .description(skillsBodyDTO.getDescription())
                .animal(animal)
                .sellerId(userDetail.getSellerId())
                .build();
        return mapperService.getSkill(skillsRepository.save(skill));
    }

    @Override
    public List<SkillsDTO> getSkills(CustomUserDetail userDetail) {
        return skillsRepository.findAllBySeller_Id(userDetail.getSellerId()).stream()
                .map(mapperService::getSkill)
                .toList();
    }

    @Override
    public SkillsDTO getSkill(CustomUserDetail userDetail, Long id) {
        Skills skill = skillsRepository.findAllByIdAndSeller_Id(id, userDetail.getSellerId())
                .orElseThrow(() -> new SkillNotFoundException("This skill has not been found."));
        return mapperService.getSkill(skill);
    }

    @Override
    public SkillsDTO updateSkill(CustomUserDetail userDetail, Long id, SkillsPutBodyDTO skillsPutBodyDTO) {
        List<Skills> skills = skillsRepository.update(id, userDetail.getSellerId(),
                skillsPutBodyDTO.getTitle(), skillsPutBodyDTO.getDescription());
        if (skills.isEmpty()) {
            throw new SkillsNotFoundException("This skill was not found.");
        }
        return mapperService.getSkill(skillsRepository.save(skills.get(0)));
    }

    @Override
    public void deleteSkill(CustomUserDetail userDetail, Long id) {
        skillsRepository.deleteAndReturning(id, userDetail.getSellerId());
    }
}
