package com.teamsphere.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final AuthenticationProvider authProvider;

    private static final String BASE_URL = "/api/v1/";
    public static final String COMPANY_URL = BASE_URL + "company";
    public static final String DEPARTMENT_URL = BASE_URL + "department";
    public static final String EMPLOYEE_URL = BASE_URL + "employee";
    public static final String POSITION_URL = BASE_URL + "position";
    public static final String PROJECT_URL = BASE_URL + "project";
    public static final String TASK_URL = BASE_URL + "task";
    public static final String SEARCH_URL = BASE_URL + "search";
    public static final String ROLE_ADMIN = "ADMIN";

    @Bean
    public CorsConfigurationSource corsConfigurer() {
        final CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request ->
                        request
                                .requestMatchers(AUTH_WHITELIST).permitAll()
                                .requestMatchers(BASE_URL + "auth/register").permitAll()
                                .requestMatchers(BASE_URL + "auth/login").permitAll()

                                //Company
                                .requestMatchers(HttpMethod.GET, COMPANY_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, COMPANY_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, COMPANY_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, COMPANY_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, COMPANY_URL).hasRole(ROLE_ADMIN)

                                //Department
                                .requestMatchers(HttpMethod.GET, DEPARTMENT_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, DEPARTMENT_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, DEPARTMENT_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, DEPARTMENT_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, DEPARTMENT_URL).hasRole(ROLE_ADMIN)

                                //Employee
                                .requestMatchers(HttpMethod.GET, EMPLOYEE_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, EMPLOYEE_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, EMPLOYEE_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, EMPLOYEE_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, EMPLOYEE_URL).hasRole(ROLE_ADMIN)

                                //Position
                                .requestMatchers(HttpMethod.GET, POSITION_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, POSITION_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, POSITION_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, POSITION_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, POSITION_URL).hasRole(ROLE_ADMIN)

                                //Project
                                .requestMatchers(HttpMethod.GET, PROJECT_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, PROJECT_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, PROJECT_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, PROJECT_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, PROJECT_URL).hasRole(ROLE_ADMIN)

                                //Task
                                .requestMatchers(HttpMethod.GET, TASK_URL).hasAnyRole("USER", ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, TASK_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.POST, TASK_URL + SEARCH_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, TASK_URL).hasRole(ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, TASK_URL).hasRole(ROLE_ADMIN)

                                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsConfigurer -> corsConfigurer.configurationSource(corsConfigurer()))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandler ->
                        exceptionHandler.authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage())
                        ));

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }


    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };
}
