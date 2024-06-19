package ru.pin120.carwashAPI.security.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
/**
 * Класс AuthEntryPointJwt реализует интерфейс {@link AuthenticationEntryPoint}.
 * Этот класс обрабатывает несанкционированные запросы к защищенным ресурсам.
 */
public class AuthEntryPointJwt  implements AuthenticationEntryPoint {
    /**
     * Этот метод вызывается всякий раз, когда доступ к защищенному ресурсу запрашивается без авторизации
     *
     * @param request объект HttpServletRequest, содержащий запрос клиента
     * @param response объект HttpServletResponse, используемый для отправки ответа клиенту
     * @param authException объект AuthenticationException, содержащий детали исключения
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ServletException если произошла ошибка сервлета
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Ошибка: Несанкционированный доступ");
    }
}
