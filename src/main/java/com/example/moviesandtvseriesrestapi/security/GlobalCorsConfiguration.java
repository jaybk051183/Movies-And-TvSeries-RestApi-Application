package com.example.moviesandtvseriesrestapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//De klasse GlobalCorsConfiguration configureert globale CORS-instellingen voor de applicatie. Het staat cross-origin verzoeken toe van elke origin en beperkt de toegestane HTTP-methoden tot "GET", "POST", "PUT", "PATCH", "DELETE", en "OPTIONS". De mapping is van toepassing op alle URL-paden in de applicatie (aangegeven door /**).

@Configuration
public class GlobalCorsConfiguration
{
    // Definieer een Spring bean van type WebMvcConfigurer.
    // Dit biedt mogelijkheden om MVC-gerelateerde instellingen aan te passen, in dit geval CORS.
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // Retourneer een anonieme klasse die de WebMvcConfigurer interface implementeert.
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Voeg een CORS-registratie toe voor alle URL-paden (/**) van de applicatie.
                registry.addMapping("/**")
                        // Staat toe dat alle origins (domeinen) toegang hebben.
                        .allowedOrigins("*")
                        // Definieer welke HTTP-methoden zijn toegestaan voor cross-origin verzoeken.
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
            }
        };
    }
}
