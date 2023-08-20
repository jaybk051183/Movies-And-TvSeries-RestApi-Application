package com.example.moviesandtvseriesrestapi.dtos;

public class SubscriptionResponseDto {

    private String status;
    private String message;

    public SubscriptionResponseDto() {}

    public SubscriptionResponseDto(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SubscriptionResponseDto{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}