package keenay.education.mapper.tasks;

import keenay.education.dto.tasks.TaskDTO;
import keenay.education.entity.Tasks;
import keenay.education.service.image.ImageService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = ImageService.class)
public interface TaskMapper {

    @Mappings({
            @Mapping(source = "task.id", target = "id"),
            @Mapping(source = "task.title", target = "title"),
            @Mapping(source = "task.description", target = "description"),
            @Mapping(source = "task.photoUrl", target = "photoUrl",
                    qualifiedByName = "photoUrlToPresigned"),
            @Mapping(source = "task.advertisement.id", target = "advertisementId"),
            @Mapping(source = "task.status", target = "status")
    })
    TaskDTO getDTO(Tasks task);
}