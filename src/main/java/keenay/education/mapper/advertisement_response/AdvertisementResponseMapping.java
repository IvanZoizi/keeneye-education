package keenay.education.mapper.advertisement_response;

import keenay.education.dto.advertisement_response.AdvertisementResponseDTO;
import keenay.education.dto.skills.SkillsDTO;
import keenay.education.entity.AdvertisementResponse;
import keenay.education.entity.Skills;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface AdvertisementResponseMapping  {
    @Mappings({
            @Mapping(source = "advertisementResponse.id", target="id"),
            @Mapping(source = "advertisementResponse.seller.id", target = "sellerId"),
            @Mapping(source = "advertisementResponse.price", target = "price"),
            @Mapping(source = "advertisementResponse.comment", target = "comment"),
            @Mapping(source = "advertisementResponse.status", target = "status"),
            @Mapping(source = "advertisementResponse.advertisement.id", target = "advertisementId")
    })
    AdvertisementResponseDTO getDTO(AdvertisementResponse advertisementResponse);
}
