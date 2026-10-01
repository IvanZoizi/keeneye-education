package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import keenay.education.dto.auth.LoginDTO;
import keenay.education.dto.auth.RegisterAdminDTO;
import keenay.education.dto.auth.RegisterCustomerDTO;
import keenay.education.dto.auth.RegisterSellerDTO;
import keenay.education.dto.security.JwtAutorizeToken;
import keenay.education.security.CustomUserDetail;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.naming.AuthenticationException;

@Tag(name = "Auth Endpoints")
@RequestMapping("/api/v1/auth")
public interface UserController {
    String registerAdmin(@Valid @RequestBody RegisterAdminDTO registerAdminDTO) throws AuthenticationException;
    String registerCustomer(@Valid @RequestBody RegisterCustomerDTO registerCustomerDTO) throws AuthenticationException;
    String registerSeller(@Valid @RequestBody RegisterSellerDTO registerSellerDTO) throws AuthenticationException;
    JwtAutorizeToken signIn(@Valid @RequestBody LoginDTO loginDTO) throws AuthenticationException;
    String logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String bearer);
}