package com.example.moviesandtvseriesrestapi.security;

import com.example.moviesandtvseriesrestapi.services.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// De JwtRequestFilter is een filter dat elk binnenkomend verzoek inspecteert op een JWT in de "Authorization" header.
// Als zo'n token aanwezig is, wordt de gebruikersnaam uit de token gehaald.
// Het filter controleert vervolgens of de gebruiker al is geauthenticeerd. Zo niet, dan wordt de gebruiker opgehaald met de loadUserByUsername methode.
 // De token wordt gevalideerd en, indien geldig, wordt de gebruiker geauthenticeerd en wordt deze authenticatie in de beveiligingscontext van Spring gezet.
 //  Tenslotte laat het filter het verzoek doorgaan in de filterketen.

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    // Dependency injectie van de CustomUserDetailsService en JwtUtil.
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // De methode die elke keer wordt aangeroepen als een verzoek binnenkomt.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // Haal de "Authorization" header uit het binnenkomende verzoek.
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Controleer of de header bestaat en begint met "Bearer ".
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // Haal de eigenlijke token op (zonder het "Bearer " deel).
            jwt = authorizationHeader.substring(7);
            // Haal de gebruikersnaam uit de token.
            username = jwtUtil.extractUsername(jwt);
        }

        // Als we een gebruikersnaam hebben EN de huidige authenticatiecontext is leeg,
        // dan proberen we de gebruiker te authenticeren met de token.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Valideer de token tegen de details van de geladen gebruiker.
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // Maak een nieuwe authenticatie-token voor deze gebruiker en stel deze in in de beveiligingscontext.
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // Laat het verzoek doorgaan in de filterketen.
        filterChain.doFilter(request, response);

    }

}