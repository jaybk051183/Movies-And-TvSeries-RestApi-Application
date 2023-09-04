package com.example.moviesandtvseriesrestapi.services;

import com.example.moviesandtvseriesrestapi.dtos.UserDto;
import com.example.moviesandtvseriesrestapi.models.Authority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//Deze klasse zorgt voor het opzoeken van gebruikersinformatie op basis van gebruikersnaam, wat essentieel is voor authenticatie en autorisatie in een Spring Security-toepassing.

@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Declareer een UserService variabele (dit wordt gebruikt om gebruikersgegevens op te halen).
    private final UserService userService;

    // Constructor om de UserService afhankelijkheid te injecteren.
    public CustomUserDetailsService(UserService userService) {

        this.userService = userService;
    }

    // Override de loadUserByUsername methode van de UserDetailsService interface.
    @Override
    public UserDetails loadUserByUsername(String username) {
        // Haal de UserDto (Data Transfer Object) op voor de gegeven gebruikersnaam.
        UserDto userDto = userService.getUser(username);

        // Haal het wachtwoord op uit de UserDto
        String password = userDto.getPassword();

        // Haal de autoriteiten (rollen/rechten) van de gebruiker op uit de UserDto.
        Set<Authority> authorities = userDto.getAuthorities();

        // Converteer de set van Authority objecten naar een lijst van GrantedAuthority objecten (compatibel met Spring Security).
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Authority authority: authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }

       // Retourneer een nieuwe User object met de gebruikersnaam, wachtwoord en de lijst van autoriteiten.
        return new org.springframework.security.core.userdetails.User(username, password, grantedAuthorities);
    }

}


/*
    De CustomUserDetailsService is een maatwerk service die gegevens ophaalt voor een gebruiker op basis van hun gebruikersnaam. Het haalt gebruikersgegevens op uit een andere service (UserService), zet de autoriteiten om in een formaat dat door Spring Security wordt begrepen, en retourneert een UserDetails object dat door Spring Security kan worden gebruikt voor authenticatie en autorisatie.*/
