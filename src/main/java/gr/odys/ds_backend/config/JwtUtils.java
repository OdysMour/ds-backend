package gr.odys.ds_backend.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Value("${app.jwtIssuer:ds-backend}")
    private String jwtIssuer;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + jwtExpirationMs);

        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .header()
                    .type("JWT")
                    .and()
                .subject(userPrincipal.getUsername())
                .issuedAt(createdDate)
                .expiration(expirationDate)
                .issuer(jwtIssuer)
                .id(java.util.UUID.randomUUID().toString())
                .claim("roles", authorities)
                .signWith(key, Jwts.SIG.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired");
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported");
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty");
        }
        return false;
    }
}