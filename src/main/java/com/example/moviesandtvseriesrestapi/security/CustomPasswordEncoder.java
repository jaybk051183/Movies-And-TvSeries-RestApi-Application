package com.example.moviesandtvseriesrestapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//BCrypt algoritme om wachtwoorden te hashen: wordt veel moeilijker om terug te gaan naar het originele wachtwoord, en elke codering is uniek.

@Configuration
public class CustomPasswordEncoder {

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }
}