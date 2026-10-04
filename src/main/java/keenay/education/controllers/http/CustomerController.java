package keenay.education.controllers.http;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import keenay.education.dto.users.CustomerBodyDTO;
import keenay.education.dto.users.CustomerDTO;
import keenay.education.security.CustomUserDetail;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Customer Endpoints")
@RequestMapping("/api/v1/customer")
public interface CustomerController {
    CustomerDTO getCustomer(@AuthenticationPrincipal CustomUserDetail userDetail);
    CustomerDTO updateCustomer(@AuthenticationPrincipal CustomUserDetail userDetail,
                               @RequestBody CustomerBodyDTO customerBodyDTO);

}
