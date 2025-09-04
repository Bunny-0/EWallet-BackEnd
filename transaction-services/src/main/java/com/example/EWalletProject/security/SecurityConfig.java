package com.example.EWalletProject.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class SecurityConfig {




    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception{

        http.authorizeHttpRequests((req)->{
            req.antMatchers("/api/").permitAll().anyRequest().authenticated();
        }).formLogin();
        return http.build();


    }
}
