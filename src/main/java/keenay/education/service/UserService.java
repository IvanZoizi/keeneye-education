package keenay.education.service;

import keenay.education.dto.auth.LoginDTO;
import keenay.education.dto.auth.RegisterAdminDTO;
import keenay.education.dto.auth.RegisterCustomerDTO;
import keenay.education.dto.auth.RegisterSellerDTO;
import keenay.education.dto.security.JwtAutorizeToken;
import keenay.education.security.CustomUserDetail;

import javax.naming.AuthenticationException;

public interface UserService {
    String registerAdmin(RegisterAdminDTO registerAdminDTO) throws AuthenticationException;
    String registerCustomer(RegisterCustomerDTO registerCustomerDTO) throws AuthenticationException;
    String registerSeller(RegisterSellerDTO registerSellerDTO) throws AuthenticationException;
    JwtAutorizeToken singIn(LoginDTO loginDTO) throws AuthenticationException;
    String logout(String token);
}
