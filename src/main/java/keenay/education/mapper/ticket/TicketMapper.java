package keenay.education.mapper.ticket;

import keenay.education.dto.support.TicketDTO;
import keenay.education.dto.support.TicketWithAnswerDTO;
import keenay.education.entity.Ticket;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface TicketMapper {
    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "question", target = "question"),
            @Mapping(target = "answer", expression = "java(getAnswer(ticket))")
    })
    TicketWithAnswerDTO getTicketWithAnswerDTO(Ticket ticket);

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "question", target = "question"),
    })
    TicketDTO getTicketDTO(Ticket ticket);

    default String getAnswer(Ticket ticket) {
        return ticket.getTicketReplies() != null
                ? ticket.getTicketReplies().getAnswer()
                : "";
    }
}
