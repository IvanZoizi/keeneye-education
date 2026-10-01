package keenay.education.security;

import keenay.education.entity.Roles;
import keenay.education.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetail extends User {
    private final Long userId;
    private final Long sellerId;
    private final Long customerId;

    public CustomUserDetail(Long userId, Long customerId, Long sellerId, String email, String password,
                            List<String> roles) {
        super(email, password, buildAuthority(roles));
        this.userId = userId;
        this.customerId = customerId;
        this.sellerId = sellerId;
    }

    private static Collection<? extends GrantedAuthority> buildAuthority(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
}
