package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.dto.users.SellerBodyDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Seller Endpoints")
@RequestMapping("/api/v1/seller")
public interface SellersController {
    SellerDTO getSeller(@AuthenticationPrincipal CustomUserDetail userDetail);
    SellerDTO updateSeller(@AuthenticationPrincipal CustomUserDetail userDetail,
                               @RequestBody SellerBodyDTO customerBodyDTO);
}
