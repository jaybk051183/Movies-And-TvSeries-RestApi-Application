package com.example.moviesandtvseriesrestapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

//De klasse CustomPasswordEncoder definieert een Spring bean voor PasswordEncoder die gebruik maakt van het BCrypt algoritme om wachtwoorden te coderen/hashen
//Wanneer een wachtwoord wordt gecodeerd met BCrypt, wordt het veel moeilijker om terug te gaan naar het originele wachtwoord, en elke codering is uniek.

@Configuration
public class CustomPasswordEncoder {

    // Definieer een Spring bean voor de PasswordEncoder.
    // Wanneer andere delen van de applicatie een PasswordEncoder bean nodig hebben, zullen ze deze versie krijgen.
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Retourneer een nieuwe instantie van BCryptPasswordEncoder, wat een implementatie is van PasswordEncoder.
        // Dit gebruikt het BCrypt algoritme om wachtwoorden te hashen.
        return new BCryptPasswordEncoder();
    }
}