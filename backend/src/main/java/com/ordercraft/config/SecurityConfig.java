package com.ordercraft.config;

import com.ordercraft.auth.BearerTokenFilter;
import com.ordercraft.auth.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public BearerTokenFilter bearerTokenFilter(AuthService authService) { return new BearerTokenFilter(authService); }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, BearerTokenFilter bearerTokenFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup", "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/auth/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/customers/**", "/api/products/**", "/api/materials/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Please sign in to continue\"}");
                        })
                        .accessDeniedHandler((request, response, exception) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"Your account does not have permission for this action\"}");
                        }))
                .addFilterBefore(bearerTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
