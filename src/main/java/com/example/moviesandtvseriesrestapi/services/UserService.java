package com.example.moviesandtvseriesrestapi.services;

import com.example.moviesandtvseriesrestapi.dtos.UserDto;
import com.example.moviesandtvseriesrestapi.models.Authority;
import com.example.moviesandtvseriesrestapi.models.User;
import com.example.moviesandtvseriesrestapi.repositories.UserRepository;
import com.example.moviesandtvseriesrestapi.utils.RandomStringGenerator;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

//User story 1: 1. Customer can log in

@Service
public class UserService {

    private final UserRepository repos;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repos, PasswordEncoder passwordEncoder) {
        this.repos = repos;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return repos.findByEmail(email);
    }

    public List<UserDto> getUsers() {
        List<UserDto> collection = new ArrayList<>();
        List<User> list = repos.findAll();
        for (User user : list) {
            collection.add(fromUser(user));
        }
        return collection;
    }

    public UserDto getUser(String username) {
        UserDto dto = new UserDto();
        Optional<User> user = repos.findById(username);
        if (user.isPresent()){
            dto = fromUser(user.get());
        }else {
            throw new UsernameNotFoundException(username);
        }
        return dto;
    }

    public boolean userExists(String username) {
        return repos.existsById(username);
    }

    public String createUser(UserDto userDto) {
        String randomString = RandomStringGenerator.generateAlphaNumeric(20);
        userDto.setApikey(randomString);
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User newUser = repos.save(toUser(userDto));
        return newUser.getUsername();
    }

    public void deleteUser(String username) {
        repos.deleteById(username);
    }

    public void updateUser(String username, UserDto newUser) {
        if (!repos.existsById(username)) throw new UsernameNotFoundException(username);
        User user = repos.findById(username).get();
        user.setPassword(newUser.getPassword());
        user.setEmail(newUser.getEmail());
        repos.save(user);
    }

    public Set<Authority> getAuthorities(String username) {
        if (!repos.existsById(username)) throw new UsernameNotFoundException(username);
        User user = repos.findById(username).get();
        UserDto userDto = fromUser(user);
        return userDto.getAuthorities();
    }

    public void addAuthority(String username, String authority) {

        if (!repos.existsById(username)) throw new UsernameNotFoundException(username);
        User user = repos.findById(username).get();
        user.addAuthority(new Authority(username, authority));
        repos.save(user);
    }

    public void removeAuthority(String username, String authority) {
        if (!repos.existsById(username)) throw new UsernameNotFoundException(username);
        User user = repos.findById(username).get();
        Authority authorityToRemove = user.getAuthorities().stream().filter((a) -> a.getAuthority().equalsIgnoreCase(authority)).findAny().get();
        user.removeAuthority(authorityToRemove);
        repos.save(user);
    }

    public static UserDto fromUser(User user){

        var dto = new UserDto();

        dto.username = user.getUsername();
        dto.password = user.getPassword();
        dto.enabled = user.isEnabled();
        dto.apikey = user.getApikey();
        dto.email = user.getEmail();
        dto.authorities = user.getAuthorities();

        return dto;
    }

    public User toUser(UserDto userDto) {

        var user = new User();

        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setEnabled(userDto.getEnabled());
        user.setApikey(userDto.getApikey());
        user.setEmail(userDto.getEmail());

        return user;
    }

}