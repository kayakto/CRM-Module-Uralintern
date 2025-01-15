package org.bitebuilders.config;

import org.bitebuilders.component.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private final String[] allowedPatternsAnon = new String[] {
            "/swagger-ui/**",  // Swagger UI
            "/v3/api-docs/**", // OpenAPI JSON
            "/auth/register",
            "/auth/login",
            "/auth/refresh",
            "/swagger.json", // Разрешаем доступ к custom-swagger.json
            "api/swagger-ui/**",  // Swagger UI
            "api/v3/api-docs/**", // OpenAPI JSON
            "api/auth/register",
            "api/auth/login",
            "api/auth/refresh",
            "api/swagger.json" // Разрешаем доступ к custom-swagger.json
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
//                        .csrfTokenRepository(csrfTokenRepository())
//                        .ignoringRequestMatchers(
//                        allowedPatternsAnon)) // Отключение CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allowedPatternsAnon)
                        .permitAll() // Разрешаем доступ к этим эндпоинтам
                        .anyRequest().authenticated() // Остальные запросы требуют авторизации
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Добавляем фильтр

        return http.build();
    }

//    @Bean
//    public CsrfTokenRepository csrfTokenRepository() {
//        return new HttpSessionCsrfTokenRepository(); // Использование сессии для хранения CSRF токенов
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Для хеширования паролей
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}


