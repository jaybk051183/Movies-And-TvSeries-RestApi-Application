package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.UserDto;
import com.example.moviesandtvseriesrestapi.exceptions.BadRequestException;
import com.example.moviesandtvseriesrestapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

// API aangeroepen vanuit een andere domeinnaam dan localhost 8080 middels crossorigin annotatie
@CrossOrigin
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private final UserService userService;

    //Constructor-based dependency injection:
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Ophalen van een lijst van alle gebruikers:
    @GetMapping(value = "")
    public ResponseEntity<List<UserDto>> getUsers() {

        List<UserDto> userDtos = userService.getUsers();

        return ResponseEntity.ok().body(userDtos);
    }

    //Ophalen van een specifieke gebruiker op basis van gebruikersnaam:
    @GetMapping(value = "/{username}")
    public ResponseEntity<UserDto> getUser(@PathVariable("username") String username) {

        UserDto optionalUser = userService.getUser(username);

        return ResponseEntity.ok().body(optionalUser);

    }

    //Creëren van een nieuwe gebruiker:
    @PostMapping(value = "")
    public ResponseEntity<UserDto> createKlant(@RequestBody UserDto dto) {
        //Gebruiker aanmaken:
        String newUsername = userService.createUser(dto);
        //Autoriteit toevoegen aan de nieuwe gebruiker:
        userService.addAuthority(newUsername, "ROLE_USER");

        //Locatie van de nieuwe gebruiker genereren:
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}")
                .buildAndExpand(newUsername).toUri();

        //HTTP Response retourneren (201 created)
        return ResponseEntity.created(location).build();
    }

    //Bijwerken van gegevens van een bestaande gebruiker:
    @PutMapping(value = "/{username}")
    public ResponseEntity<UserDto> updateKlant(@PathVariable("username") String username, @RequestBody UserDto dto) {

        userService.updateUser(username, dto);

        //204 No Content
        return ResponseEntity.noContent().build();
    }

    //Het verwijderen van een gebruiker op basis van gebruikersnaam:
    @DeleteMapping(value = "/{username}")
    public ResponseEntity<Object> deleteKlant(@PathVariable("username") String username) {
        userService.deleteUser(username);
        return ResponseEntity.noContent().build();
    }

    //Ophalen van de autoriteiten/rollen van een specifieke gebruiker:
    @GetMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> getUserAuthorities(@PathVariable("username") String username) {
        return ResponseEntity.ok().body(userService.getAuthorities(username));
    }

    //Het toevoegen van een autoriteit/rol aan een specifieke gebruiker:
    @PostMapping(value = "/{username}/authorities")
    public ResponseEntity<Object> addUserAuthority(@PathVariable("username") String username, @RequestBody Map<String, Object> fields) {
        try {
            String authorityName = (String) fields.get("authority");
            userService.addAuthority(username, authorityName);
            return ResponseEntity.noContent().build();
        }
        catch (Exception ex) {
            throw new BadRequestException();
        }
    }

    //Het verwijderen van een specifieke autoriteit/rol van een gebruiker:
    @DeleteMapping(value = "/{username}/authorities/{authority}")
    public ResponseEntity<Object> deleteUserAuthority(@PathVariable("username") String username, @PathVariable("authority") String authority) {
        userService.removeAuthority(username, authority);
        return ResponseEntity.noContent().build();
    }

}
