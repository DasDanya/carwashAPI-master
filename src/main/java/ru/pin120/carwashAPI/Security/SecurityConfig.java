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

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;
    private final UserDetailsServiceImpl userDetailsServiceImpl;

    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.unauthorizedHandler = unauthorizedHandler;
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsServiceImpl);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

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
                        auth.requestMatchers("/api/users/**").permitAll()
                                .requestMatchers("/api/boxes/**").hasAnyAuthority(UserRole.ADMINISTRATOR.name(), UserRole.OWNER.name())
                                .requestMatchers("/api/typeProducts/add/**", "/api/typeProducts/update/**", "/api/typeProducts/delete/**").hasAnyAuthority("ADMINISTRATOR")
                                .requestMatchers("/api/products/add/**", "/api/products/update/**", "/api/products/delete/**").hasAnyAuthority("CUSTOMER")
                                .requestMatchers("/api/printingHouses/**").hasAnyAuthority("ADMINISTRATOR")
                                .requestMatchers("/api/materials/add/**", "/api/materials/update/**", "/api/materials/delete/**").hasAnyAuthority("ADMINISTRATOR")
                                .requestMatchers("/api/employees/**").permitAll()
                                .requestMatchers("/api/bookings/add/**", "/api/bookings/delete/**").hasAnyAuthority("CUSTOMER")
                                .anyRequest()
                                .authenticated());

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
