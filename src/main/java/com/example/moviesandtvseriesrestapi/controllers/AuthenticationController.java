package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.AuthenticationRequest;
import com.example.moviesandtvseriesrestapi.dtos.AuthenticationResponse;
import com.example.moviesandtvseriesrestapi.security.JwtUtil;
import com.example.moviesandtvseriesrestapi.services.CustomUserDetailsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

// API aangeroepen vanuit een andere domeinnaam dan localhost 8080 middels crossorigin annotatie
@CrossOrigin
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    //Constructor-based dependency injection:
    public AuthenticationController(AuthenticationManager authenticationManager, CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    //Controleer wie nu de geauthenticeerde gebruiker is:
    @GetMapping(value = "/authenticated")
    public ResponseEntity<Object> authenticated(Authentication authentication, Principal principal) {
        //Retourneer een nieuw ResponseEntity object met een HTTP-status 200 en voeg de principal (de geauthenticeerde gebruiker) toe aan de body van de HTTP-respons:
        return ResponseEntity.ok().body(principal);
    }

    // Een gebruiker dient inloggegevens in:
    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        //Uitlezen van Gebruikersgegevens:
        String username = authenticationRequest.getUsername();
        String password = authenticationRequest.getPassword();

        //Authenticatie Proces:
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        }

       // Als de inloggegevens ongeldig zijn, retourneer dan een foutbericht:
        catch (BadCredentialsException ex) {
            return ResponseEntity.badRequest().body(new AuthenticationResponse(null, "Login failed", "Incorrect username or password"));
        }

       // Als de inloggegevens geldig zijn, retourneer dan een JWT-token dat kan worden gebruikt voor verzoeken naar de API:
        final UserDetails userDetails = userDetailsService
                .loadUserByUsername(username);

        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt, "Login successful", "Welkom, you are logged in successfully!"));
    }

}
