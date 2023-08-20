package com.example.moviesandtvseriesrestapi.dtos;

public class SubscriptionRequestDto {

    private String email;
    private String availableCategory;

    public SubscriptionRequestDto() {}

    public SubscriptionRequestDto(String email, String availableCategory) {
        this.email = email;
        this.availableCategory = availableCategory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvailableCategory() {
        return availableCategory;
    }

    public void setAvailableCategory(String availableCategory) {
        this.availableCategory = availableCategory;
    }

    @Override
    public String toString() {
        return "SubscriptionRequestDTO{" +
                "email='" + email + '\'' +
                ", availableCategory='" + availableCategory + '\'' +
                '}';
    }
}