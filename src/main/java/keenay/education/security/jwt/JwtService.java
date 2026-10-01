package keenay.education.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import keenay.education.dto.security.JwtAutorizeToken;
import keenay.education.entity.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtService {

    private final String jwtSecret;
    private final Integer daysExpired;

    public JwtService(@Value("${spring.security.key}") String jwtSecret,
                      @Value("${spring.security.days}") Integer days) {
        this.jwtSecret = jwtSecret;
        this.daysExpired = days;
    }

    public JwtAutorizeToken generateAuthToken(String login, List<Roles> roles) {
        List<String> roleNames = roles.stream().map(Roles::getRole).toList();

        JwtAutorizeToken jwtDto = new JwtAutorizeToken(generateJwtToken(login, roleNames));
        return jwtDto;
    }

    public String getLoginFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRolesFromToken(String token) {
        return parseClaims(token).get("roles", List.class);
    }

    public String generateJwtToken(String login, List<String> roles) {
        Date date = Date.from(LocalDateTime.now().plusDays(this.daysExpired).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(login)
                .claim("roles", roles)
                .expiration(date)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateJwtToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JwtException", ex);
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JwtException", ex);
        } catch (MalformedJwtException ex) {
            log.error("Malformed JwtException", ex);
        } catch (SecurityException ex) {
            log.error("Security Exception", ex);
        } catch (Exception ex) {
            log.error("Invalid token", ex);
        }
        return false;
    }

    public String getJwtToken(String authHeader) {
        String jwtToken = extractBearer(authHeader);
        if (!validateJwtToken(jwtToken)) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return jwtToken;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private String extractBearer(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}