package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.CustomerController;
import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.pl.REGON;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_customer')")
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService customerService;

    @Override
    @GetMapping
    public CustomerDTO getCustomer(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return customerService.getCustomer(userDetail);
    }

    @Override
    @PutMapping
    public CustomerDTO updateCustomer(@AuthenticationPrincipal CustomUserDetail userDetail, @Valid @RequestBody CustomerBodyDTO customerBodyDTO) {
        return customerService.updateCustomer(userDetail, customerBodyDTO);
    }
}
