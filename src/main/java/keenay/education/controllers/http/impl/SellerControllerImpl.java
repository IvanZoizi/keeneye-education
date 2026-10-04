package keenay.education.controllers.http.impl;

import jakarta.validation.Valid;
import keenay.education.controllers.http.SellersController;
import keenay.education.dto.users.SellerBodyDTO;
import keenay.education.dto.users.SellerDTO;
import keenay.education.security.CustomUserDetail;
import keenay.education.service.SellerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_seller')")
public class SellerControllerImpl implements SellersController {
    private final SellerService sellerService;

    @Override
    @GetMapping
    public SellerDTO getSeller(@AuthenticationPrincipal CustomUserDetail userDetail) {
        return sellerService.getSeller(userDetail);
    }

    @Override
    @PutMapping
    public SellerDTO updateSeller(@AuthenticationPrincipal CustomUserDetail userDetail,
                                  @Valid @RequestBody SellerBodyDTO customerBodyDTO) {
        return sellerService.updateSeller(userDetail, customerBodyDTO);
    }
}
