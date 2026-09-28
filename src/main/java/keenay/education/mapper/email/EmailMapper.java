package keenay.education.mapper.email;

import keenay.education.dto.email.EmailDTO;
import keenay.education.entity.EmailsUser;
import keenay.education.entity.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface EmailMapper {
    @Mappings({
            @Mapping(source = "userId", target="userId"),
            @Mapping(source = "email", target = "email"),
            @Mapping(source = "text", target = "text"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "attempt", target = "attempt"),
            @Mapping(source = "emailsUserId", target = "emailsUserId")
    })
    EmailDTO getDTO(Long userId, String email, String text, String subject, Integer attempt, Long emailsUserId);

    @Mappings({
            @Mapping(source = "userId", target="userId"),
            @Mapping(source = "text", target = "text"),
            @Mapping(source = "subject", target = "subject"),
            @Mapping(source = "attempt", target = "attempt"),
    })
    EmailsUser getEmailsUser(Long userId, String text, String subject, Integer attempt);
}
