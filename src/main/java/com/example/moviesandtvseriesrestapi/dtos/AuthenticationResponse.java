package com.example.moviesandtvseriesrestapi.dtos;

public class AuthenticationResponse {

    private final String jwt;
    private final String status;
    private final String message;

    public AuthenticationResponse(String jwt, String status, String message) {
        this.jwt = jwt;
        this.status = status;
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}