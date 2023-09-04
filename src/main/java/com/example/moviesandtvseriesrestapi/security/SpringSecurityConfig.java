package com.example.moviesandtvseriesrestapi.security;

import com.example.moviesandtvseriesrestapi.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//De klasse SpringSecurityConfig definieert de beveiligingsconfiguratie voor de Spring Boot applicatie.
// authenticationManager bepaalt hoe gebruikers geauthenticeerd worden, met name welke service gebruikt wordt om gebruikers te laden en welke encoder wordt gebruikt voor wachtwoorden.
  // In de methode filter, worden de beveiligingsregels voor verschillende endpoints gedefinieerd. Sommige paden zijn open voor iedereen, sommige vereisen dat de gebruiker de rol 'ADMIN' of 'USER' heeft, en sommige vereisen gewoon dat de gebruiker is geauthenticeerd.
 // CSRF-bescherming en HTTP Basic authenticatie zijn uitgeschakeld.
 // CORS is ingeschakeld.
// De applicatie is ingesteld om stateless sessies te gebruiken, wat betekent dat het geen sessie-informatie opslaat tussen verzoeken. Dit is typisch voor JWT-gebaseerde authenticatie.
   // Het jwtRequestFilter wordt toegevoegd aan de filterketen. Dit filter controleert elk binnenkomend verzoek op de aanwezigheid van een geldige JWT.

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final PasswordEncoder passwordEncoder;

    public SpringSecurityConfig(CustomUserDetailsService customUserDetailsService, JwtRequestFilter jwtRequestFilter, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.passwordEncoder = passwordEncoder;
    }

    // Configuratie van de AuthenticationManager. Deze bepaalt hoe gebruikers geverifieerd worden.
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    // Configuratie van de SecurityFilterChain. Dit bepaalt hoe binnenkomende verzoeken worden gecontroleerd.
    @Bean
    protected SecurityFilterChain filter(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .httpBasic().disable()
                .cors().and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/categories").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/categories/subscribe").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/categories/view").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions").hasRole("USER")
                .requestMatchers(HttpMethod.GET, "/api/v1/payment-due-date/**").hasRole("USER")
                .requestMatchers(HttpMethod.POST, "/api/v1/subscriptions/share").hasRole("USER")
                .requestMatchers("/authenticated").authenticated()
                .requestMatchers("/authenticate").permitAll()
                .anyRequest().denyAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
