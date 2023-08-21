package com.example.moviesandtvseriesrestapi.dtos;

public class SubscriptionRequestDto {

    private String email;
    private Long availableCategory;

    public SubscriptionRequestDto() {}

    public SubscriptionRequestDto(String email, Long availableCategory) {
        this.email = email;
        this.availableCategory = availableCategory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getAvailableCategory() {
        return availableCategory;
    }

    public void setAvailableCategory(Long availableCategory) {
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