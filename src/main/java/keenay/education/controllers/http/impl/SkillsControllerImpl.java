package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.SkillsController;
import keenay.education.dto.skills.SkillsBodyDTO;
import keenay.education.dto.skills.SkillsDTO;
import keenay.education.dto.skills.SkillsPutBodyDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SkillsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_seller')")
public class SkillsControllerImpl implements SkillsController {

    private final SkillsService skillsService;

    @PostMapping
    public SkillsDTO createSkillForUser(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Valid @RequestBody SkillsBodyDTO skillsBodyDTO
    ) {
        return skillsService.createSkillForUser(userDetail, skillsBodyDTO);
    }

    @GetMapping
    public List<SkillsDTO> getSkills(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return skillsService.getSkills(userDetail);
    }

    @GetMapping("/{id}")
    public SkillsDTO getSkill(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        return skillsService.getSkill(userDetail, id);
    }

    @PutMapping("/{id}")
    public SkillsDTO updateSkill(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id,
            @Valid @RequestBody SkillsPutBodyDTO skillsPutBodyDTO
    ) {
        return skillsService.updateSkill(userDetail, id, skillsPutBodyDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteSkill(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long id
    ) {
        skillsService.deleteSkill(userDetail, id);
    }
}