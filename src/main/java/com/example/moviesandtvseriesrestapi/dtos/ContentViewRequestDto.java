package com.example.moviesandtvseriesrestapi.dtos;

public class ContentViewRequestDto {

    private String email;
    private String categoryName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "ContentViewRequestDto{" +
                "email='" + email + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
