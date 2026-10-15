
package com.api_vendor.vendor_main.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ApiSecurityConfig {

    // =========================================
    // 1. HTTP-KÄYTTÖOIKEUDET
    // =========================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth

                        // Employee-raportit.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reports/employee/**"
                        )
                        .hasAnyRole("EMPLOYEE", "ADMIN")

                        // Report Viewer -raportit.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/reports/report-viewer/**"
                        )
                        .hasAnyRole("REPORT_VIEWER", "ADMIN")

                        // Muut olemassa olevat API-endpointit
                        // ovat ADMIN-roolin käytössä.
                        .anyRequest()
                        .hasRole("ADMIN")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public UserDetailsService userDetailsService(
            PasswordEncoder passwordEncoder,

            @Value("${app.api.users.employee.username}")
            String employeeUsername,

            @Value("${app.api.users.employee.password}")
            String employeePassword,

            @Value("${app.api.users.report-viewer.username}")
            String reportViewerUsername,

            @Value("${app.api.users.report-viewer.password}")
            String reportViewerPassword,

            @Value("${app.api.users.admin.username}")
            String adminUsername,

            @Value("${app.api.users.admin.password}")
            String adminPassword
    ) {

        UserDetails employee = User.builder()
                .username(employeeUsername)
                .password(
                        passwordEncoder.encode(employeePassword)
                )
                .roles("EMPLOYEE")
                .build();

        UserDetails reportViewer = User.builder()
                .username(reportViewerUsername)
                .password(
                        passwordEncoder.encode(reportViewerPassword)
                )
                .roles("REPORT_VIEWER")
                .build();

        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(
                        passwordEncoder.encode(adminPassword)
                )
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(
                employee,
                reportViewer,
                admin
        );
    }
}
