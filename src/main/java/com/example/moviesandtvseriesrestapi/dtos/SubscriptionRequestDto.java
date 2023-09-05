package com.example.moviesandtvseriesrestapi.dtos;

public class SubscriptionRequestDto {

    private String email;

    private Long availableCategory;

    private String categoryName;

    public SubscriptionRequestDto() {}

    public SubscriptionRequestDto(String email, Long availableCategory, String categoryName) {
        this.email = email;
        this.availableCategory = availableCategory;
        this.categoryName = categoryName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "SubscriptionRequestDTO{" +
                "email='" + email + '\'' +
                ", availableCategory='" + availableCategory + '\'' +
                '}';
    }
}