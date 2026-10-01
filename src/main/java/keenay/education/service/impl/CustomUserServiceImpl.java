package keenay.education.service.impl;

import keenay.education.entity.JwtInfo;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.security.CustomUserDetail;
import keenay.education.security.jwt.JwtFilter;
import keenay.education.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserServiceImpl {

    private final JwtService jwtService;
    private final JwtFilter jwtFilter;

    public CustomUserDetail getUserByEmail(String token) {
        JwtInfo jwtInfo = jwtFilter.getJwtInfoFromCache(token);
        if (!jwtFilter.isTokenValid(jwtInfo)) {
            throw new AccessDeniedException("jwt token is died");
        }
        List<String> roles = jwtService.getRolesFromToken(token);
        return new CustomUserDetail(
                jwtInfo.getUserId(),
                jwtInfo.getCustomerId(),
                jwtInfo.getSellerId(),
                jwtInfo.getEmail(),
                jwtInfo.getPassword(),
                roles
        );
    }
}
