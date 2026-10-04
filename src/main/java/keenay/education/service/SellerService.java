package keenay.education.service;

import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.dto.users.SellerBodyDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.security.CustomUserDetail;

public interface SellerService {
    SellerDTO getSeller(CustomUserDetail userDetail);
    SellerDTO updateSeller(CustomUserDetail userDetail, SellerBodyDTO sellerBodyDTO);
}
