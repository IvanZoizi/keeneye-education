package keenay.education.mapper.customer;

import keenay.education.dto.users.CustomerDTO;
import keenay.education.entity.Customers;
import keenay.education.entity.CustomersCache;
import keenay.education.mapper.TarantoolMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper extends TarantoolMapper<CustomersCache, CustomerDTO> {
    CustomerDTO getCustomerDTO(Customers customer);

    @Override
    CustomerDTO getDtoCache(CustomersCache customersCache);

    @Override
    CustomersCache toEntity(CustomerDTO customerDTO);
}