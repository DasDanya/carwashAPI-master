package ru.pin120.carwashAPI.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.pin120.carwashAPI.security.details.UserDetailsImpl;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {
    @Autowired
    private Environment environment;

    public String getUsernameFromJwtToken(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(environment.getProperty("JWT_SECRET_KEY"));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(UserDetailsImpl userPrincipal){
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(environment.getProperty("JWT_EXPIRATION_MS"))))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

//    public String generateJwtToken(Authentication authentication){
//        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
//
//        return Jwts.builder()
//                .setSubject((userPrincipal.getUsername()))
//                .setIssuedAt(new Date())
//                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(environment.getProperty("JWT_EXPIRATION_MS"))))
//                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
//                .compact();
//    }

    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(authToken);
            return true;
        }  catch (SignatureException e) {
            log.error("Неверная JWT подпись: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Неверный JWT токен: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Срок действия JWT токена истек: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT токен не поддерживается: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Строка JWT claims пуста: {}", e.getMessage());
        }

        return false;
    }

}
