package keenay.education.security;

import keenay.education.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetail extends User {
    private final Users user;

    public CustomUserDetail(Users user) {
        super(user.getEmail(), user.getPassword(), buildAuthority(user));
        this.user = user;
    }

    private static Collection<? extends GrantedAuthority> buildAuthority(Users user) {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                .collect(Collectors.toList());
    }
}
