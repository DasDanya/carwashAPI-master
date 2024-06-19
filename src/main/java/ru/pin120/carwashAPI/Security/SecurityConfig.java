package ru.pin120.carwashAPI.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import ru.pin120.carwashAPI.models.UserRole;
import ru.pin120.carwashAPI.security.jwt.AuthEntryPointJwt;
import ru.pin120.carwashAPI.security.jwt.AuthTokenFilter;
import ru.pin120.carwashAPI.services.UserDetailsServiceImpl;

import java.util.List;

/**
 * Класс SecurityConfig конфигурирует параметры безопасности для приложения
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    /**
     * Внедрение зависимостей
     *
     * @param unauthorizedHandler обработчик несанкционированного доступа
     * @param userDetailsServiceImpl сервис деталей пользователя
     */
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    /**
     * Создает фильтр для аутентификации JWT токенов
     *
     * @return экземпляр AuthTokenFilter
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Создает и конфигурирует провайдер аутентификации DAO
     *
     * @return экземпляр DaoAuthenticationProvider
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Создает менеджер аутентификации
     *
     * @param authConfig конфигурация аутентификации
     * @return экземпляр AuthenticationManager
     * @throws Exception в случае ошибки создания менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Создает и конфигурирует кодировщик паролей
     *
     * @return экземпляр PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Конфигурирует цепочку фильтров безопасности
     *
     * @param http объект HttpSecurity для конфигурации
     * @return настроенная цепочка фильтров безопасности
     * @throws Exception в случае ошибки конфигурации цепочки фильтров
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception-> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session-> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfiguration = new CorsConfiguration();
                    corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfiguration.setAllowedHeaders(List.of("*"));
                    corsConfiguration.setAllowCredentials(true);
                    return corsConfiguration;
                }))
                .authorizeHttpRequests(auth->
                        auth.requestMatchers("/api/users/register","api/users/login").permitAll()
                                .requestMatchers("/api/users", "/api/users/delete/**", "/api/users/editPassword/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/boxes", "/api/boxes/available", "/api/boxes/edit/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/boxes/create/**", "/api/boxes/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/bookings/boxBookings/**", "/api/bookings", "/api/bookings/getInfo/**", "/api/bookings/getInfoAboutWorkOfCleaner/**", "/api/bookings/create/**", "/api/bookings/newStatus/**", "/api/bookings/edit/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers( "/api/bookings/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/categoriesOfSupplies").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/categoriesOfSupplies/create/**", "/api/categoriesOfSupplies/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/categoryOfServices", "/api/categoryOfServices/getAllCatNames", "/api/categoryOfServices/getCatNamesByParameter/**", "/api/categoryOfServices/getCategoriesWithServices").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/categoryOfServices/create/**", "/api/categoryOfServices/edit/**", "/api/categoryOfServices/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/categoriesOfTransport", "/api/categoriesOfTransport/availableCategories", "/api/categoriesOfTransport/","/api/emptyCategoryTransport/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/categoriesOfTransport/create/**", "/api/categoriesOfTransport/edit/**", "/api/categoriesOfTransport/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/cleaners/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/clients/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/clientsTransport/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/priceList/", "/api/priceList/getPriceListOfCategoryTransport", "/api/priceList").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/priceList/create/**", "/api/priceList/edit/**", "/api/priceList/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/services/", "/api/services/getByServName/**","/api/services/get/**", "/api/services").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/services/create/**", "/api/services/necessaryCategoriesOfSupplies/**", "/api/services/bindServicesToCategory/**", "/api/services/bindServiceToCategory/**", "/api/services/delete/**").hasAuthority(UserRole.OWNER.name())

                                .requestMatchers("/api/suppliesInBox/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/supplies/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/transport/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())

                                .requestMatchers("/api/workSchedule/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .anyRequest()
                                .authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
