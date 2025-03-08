package com.springboot.dev_spring_boot_demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MD5PasswordEncoder(); // Consider switching to BCryptPasswordEncoder
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/login", "/logout").permitAll() // Allow access to login/logout
                                .anyRequest().authenticated() // All other pages require authentication
                )
                .formLogin(form ->
                        form
                                .loginPage("/login") // Custom login page
                                .loginProcessingUrl("/authenticateTheUser") // Form POST URL
                                .defaultSuccessUrl("/dashboard", true) // Redirect to /dashboard after login
                                .permitAll()
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // Logout endpoint (POST)
                                .logoutSuccessUrl("/login?logout") // Redirect to login page after logout
                                .permitAll()
                )
                .exceptionHandling(configurer ->
                        configurer.accessDeniedPage("/accessDenied")
                );
        return http.build();
    }
}