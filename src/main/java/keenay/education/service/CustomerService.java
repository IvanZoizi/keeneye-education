package keenay.education.service;

import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.security.CustomUserDetail;

public interface CustomerService {
    CustomerDTO getCustomer(CustomUserDetail userDetail);
    CustomerDTO updateCustomer(CustomUserDetail userDetail, CustomerBodyDTO customerBody);
}
