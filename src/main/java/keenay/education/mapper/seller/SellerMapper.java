package keenay.education.mapper.seller;

import keenay.education.dto.users.CustomerDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.entity.Customers;
import keenay.education.entity.CustomersCache;
import keenay.education.entity.SellerCache;
import keenay.education.entity.Sellers;
import keenay.education.mapper.TarantoolMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SellerMapper extends TarantoolMapper<SellerCache, SellerDTO> {
    SellerDTO getSellerDTO(Sellers customer);

    @Override
    SellerDTO getDtoCache(SellerCache customersCache);

    @Override
    SellerCache toEntity(SellerDTO customerDTO);
}