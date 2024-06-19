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

/**
 * Класс JwtUtils для работы с JWT токеном, включая генерацию, валидацию и извлечение данных.
 */
@Slf4j
@Component
public class JwtUtils {
    @Autowired
    private Environment environment;

    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param token JWT токен
     * @return имя пользователя из JWT токена
     */
    public String getUsernameFromJwtToken(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Генерирует ключ для подписи на основе секретного ключа из конфигурации
     *
     * @return объект Key для подписи JWT
     */
    private Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(environment.getProperty("JWT_SECRET_KEY"));
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерирует JWT токен для аутентифицированного пользователя
     *
     * @param userPrincipal объект UserDetailsImpl, представляющий аутентифицированного пользователя
     * @return JWT токен
     */
    public String generateJwtToken(UserDetailsImpl userPrincipal){
        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(environment.getProperty("JWT_EXPIRATION_MS"))))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    /**
     * Проверяет валидность JWT токена.
     *
     * @param authToken  JWT токен
     * @return true, если токен валиден, иначе false
     */
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

    /**
     * Получение JWT токена из заголовка Authorization
     * @param authorizationHeader заголовок Authorization
     * @return JWT токен или null, если токен отсутствует или не соответствует формату
     */
    public String getJwtToken(String authorizationHeader){
        if (authorizationHeader!= null && authorizationHeader.startsWith("Bearer ")) {
             return authorizationHeader.substring(7);
        }
        return null;
    }

}
