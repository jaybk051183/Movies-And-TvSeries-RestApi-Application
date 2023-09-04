package com.example.moviesandtvseriesrestapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

//Deze klasse biedt functionaliteit om JWT's te genereren, te valideren en informatie uit tokens te extraheren. Het maakt gebruik van de io.jsonwebtoken bibliotheek om deze taken uit te voeren.

@Service
public class JwtUtil {

    // Definieer een constante SECRET_KEY met een specifieke waarde.
    private final static String SECRET_KEY = "yabbadabbadooyabbadabbadooyabbadabbadooyabbadabbadoo";

    // Decodeer de SECRET_KEY vanuit Base64 en retourneer een HMAC sleutel.
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Haal het onderwerp (subject) uit de JWT: wat de gebruikersnaam is.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Haal de verloopdatum (expiration) van de JWT op.
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Gebruik de meegeleverde claimsResolver functie om een specifieke claim uit het token te halen.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse de token met de SECRET_KEY en haal het lichaam (body) van de token op.
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    // Controleer of de verloopdatum van het token vóór de huidige datum ligt.
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Creëer een lege claims map en roep createToken aan met deze lege claims map en de gebruikersnaam uit userDetails.
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    // Bouw een nieuwe JWT met specifieke waarden en geef het geformatteerde JWT terug.
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 10))
                .signWith(getSigningKey() , SignatureAlgorithm.HS256)
                .compact();
    }

    // Controleer of de gebruikersnaam in het token overeenkomt met de gebruikersnaam in userDetails en dat het token niet is verlopen.
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

}
