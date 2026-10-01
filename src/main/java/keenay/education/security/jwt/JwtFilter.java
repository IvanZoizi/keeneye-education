package keenay.education.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import keenay.education.entity.JwtInfo;
import keenay.education.exception.errors.AccessDeniedException;
import keenay.education.repository.tarantool.JwtInfoRepository;
import keenay.education.security.CustomUserDetail;
import keenay.education.utils.UtilsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final JwtInfoRepository jwtInfoRepository;

    public String hashToken(String token) {
        return UtilsService.sha256(token);
    }

    public JwtInfo getJwtInfoFromCache(String token) {
        String tokenHash = hashToken(token);
        return jwtInfoRepository.findById(tokenHash).orElse(null);
    }

    public boolean isTokenValid(JwtInfo jwtInfo) {
        if (jwtInfo == null) return false;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return jwtInfo.getDeletedAt() == null && jwtInfo.getExpiredAt() > now;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null && jwtService.validateJwtToken(token)) {
            setCustomUserDetailsToSecurityContextHolder(token);
        }
        filterChain.doFilter(request, response);
    }

    private void setCustomUserDetailsToSecurityContextHolder(String token) {
        String login = jwtService.getLoginFromToken(token);
        JwtInfo jwtInfo = getJwtInfoFromCache(token);
        if (!isTokenValid(jwtInfo)) {
            throw new AccessDeniedException("jwt token is died");
        }
        List<String> roles = jwtService.getRolesFromToken(token);
        CustomUserDetail customUserDetails = new CustomUserDetail(
                jwtInfo.getUserId(),
                jwtInfo.getCustomerId(),
                jwtInfo.getSellerId(),
                jwtInfo.getEmail(),
                jwtInfo.getPassword(),
                roles
        );
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(customUserDetails,
                null, customUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
