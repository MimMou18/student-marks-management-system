package com.sdlc.pro.smms.config;

import com.sdlc.pro.smms.service.AdminUserDetailsService;
import com.sdlc.pro.smms.service.StudentUserDetailsService;
import com.sdlc.pro.smms.service.TeacherUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http, AdminUserDetailsService userDetailsService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/login").permitAll()
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard")
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/admin/login?logout")
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain teacherSecurityFilterChain(HttpSecurity http, TeacherUserDetailsService userDetailsService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/teacher/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/teacher/login").permitAll()
                        .anyRequest().hasRole("TEACHER")
                )
                .formLogin(form -> form
                        .loginPage("/teacher/login")
                        .loginProcessingUrl("/teacher/login")
                        .defaultSuccessUrl("/teacher/dashboard")
                )
                .logout(logout-> logout
                        .logoutUrl("/teacher/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/teacher/login?logout")
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    @Order(3)
    SecurityFilterChain studentSecurityFilterChain(HttpSecurity http, StudentUserDetailsService userDetailsService) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/student/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/student/login").permitAll()
                        .anyRequest().hasRole("STUDENT")
                )
                .formLogin(form -> form
                        .loginPage("/student/login")
                        .loginProcessingUrl("/student/login")
                        .defaultSuccessUrl("/student/dashboard")
                )
                .logout(logout -> logout
                        .logoutUrl("/student/logout")
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/student/login?logout")
                )
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
