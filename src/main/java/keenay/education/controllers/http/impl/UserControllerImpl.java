package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.UserController;
import keenay.education.dto.auth.LoginDTO;
import keenay.education.dto.auth.RegisterAdminDTO;
import keenay.education.dto.auth.RegisterCustomerDTO;
import keenay.education.dto.auth.RegisterSellerDTO;
import keenay.education.dto.security.JwtAutorizeToken;
import keenay.education.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @PostMapping("/admin")
    public String registerAdmin(@Valid @RequestBody RegisterAdminDTO registerAdminDTO) throws AuthenticationException {
        return userService.registerAdmin(registerAdminDTO);
    }

    @PostMapping("/customer")
    public String registerCustomer(@Valid @RequestBody RegisterCustomerDTO registerCustomerDTO) throws AuthenticationException {
        return userService.registerCustomer(registerCustomerDTO);
    }

    @PostMapping("/seller")
    public String registerSeller(@Valid @RequestBody RegisterSellerDTO registerSellerDTO) throws AuthenticationException {
        return userService.registerSeller(registerSellerDTO);
    }

    @PostMapping("/sign/in")
    public JwtAutorizeToken signIn(@Valid @RequestBody LoginDTO loginDTO) throws AuthenticationException {
        return userService.singIn(loginDTO);
    }

    @Override
    @PostMapping("/logout")
    public String logout(String bearer) {
        String token = bearer.substring(7);
        return userService.logout(token);
    }
}