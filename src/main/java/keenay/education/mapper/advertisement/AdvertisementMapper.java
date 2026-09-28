package keenay.education.mapper.advertisement;

import keenay.education.dto.advertisement.AdvertisementDTO;
import keenay.education.dto.tasks.TaskDTO;
import keenay.education.entity.Advertisement;
import keenay.education.entity.AdvertisementResponse;
import keenay.education.entity.Tasks;
import keenay.education.mapper.pets.PetsMapper;
import keenay.education.mapper.tasks.TaskMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {PetsMapper.class, TaskMapper.class}
)
public interface AdvertisementMapper {

    @Mapping(source = "pet", target = "petsDTO")
    @Mapping(source = "status", target = "advertisementStatus")
    @Mapping(source = "selectedResponse", target = "selectedResponse")
    @Mapping(source = "tasksList", target = "taskDTOS")
    AdvertisementDTO getDTO(Advertisement advertisement);

    default Long map(AdvertisementResponse response) {
        return response == null ? null : response.getId();
    }

    List<TaskDTO> mapTasks(List<Tasks> tasks);
}