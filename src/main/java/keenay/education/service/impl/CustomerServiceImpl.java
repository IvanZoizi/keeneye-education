package keenay.education.service.impl;

import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.entity.Customers;
import keenay.education.exception.errors.UserIsNotFoundException;
import keenay.education.mapper.customer.CustomerMapper;
import keenay.education.repository.jpa.CustomersRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final CustomersRepository customersRepository;

    @Override
    @Cacheable(value = "customers", key = "#userDetail.customerId")
    public CustomerDTO getCustomer(CustomUserDetail userDetail) {
        return customerMapper.getCustomerDTO(
                customersRepository.findById(userDetail.getCustomerId())
                        .orElseThrow(() -> new UserIsNotFoundException("This customer is not found"))
        );
    }

    @Override
    @CachePut(value = "customers", key = "#userDetail.customerId")
    public CustomerDTO updateCustomer(CustomUserDetail userDetail, CustomerBodyDTO customerBody) {
        List<Customers> customers = customersRepository.update(
                userDetail.getCustomerId(),
                customerBody.getName(),
                customerBody.getSurname()
        );
        if (customers.isEmpty()) {
            throw new UserIsNotFoundException("This customer is not found");
        }
        return customerMapper.getCustomerDTO(customers.get(0));
    }
}
