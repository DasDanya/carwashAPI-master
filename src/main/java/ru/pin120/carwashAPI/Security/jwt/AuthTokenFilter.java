package ru.pin120.carwashAPI.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.pin120.carwashAPI.services.UserDetailsServiceImpl;

import java.io.IOException;

/**
 * Класс AuthTokenFilter расширяет OncePerRequestFilter для проверки JWT токена в каждом запросе
 * Он аутентифицирует пользователя на основе токена и устанавливает аутентификационные данные в контексте безопасности
 */
public class AuthTokenFilter extends OncePerRequestFilter {
    /**
     * Экземпляр класса для работы с JWT
     */
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Данные о пользователе
     */
    @Autowired
    private UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * Метод, который фильтрует каждый запрос для проверки наличия и валидности JWT
     *
     * @param request объект HttpServletRequest, содержащий запрос клиента
     * @param response объект HttpServletResponse, используемый для отправки ответа клиенту
     * @param filterChain объект FilterChain для продолжения цепочки фильтров
     * @throws ServletException если произошла ошибка сервлета
     * @throws IOException если произошла ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt =parseJwt(request);

        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String username = jwtUtils.getUsernameFromJwtToken(jwt);

            UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(request,response);
    }

    /**
     * Метод для извлечения JWT из заголовка авторизации запроса
     *
     * @param request объект HttpServletRequest, содержащий запрос клиента
     * @return строка JWT или null, если токен отсутствует или не соответствует формату
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
            return headerAuth.substring(7);
        }

        return null;
    }


}
