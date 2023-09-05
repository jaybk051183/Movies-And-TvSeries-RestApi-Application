package com.example.moviesandtvseriesrestapi.dtos;

import java.util.List;

public class AvailableCategoriesResponseDto {

    private List<CategoryDto> availableCategories;

    private List<SubscriptionDto> subscribedCategories;

    public AvailableCategoriesResponseDto() {}

    public AvailableCategoriesResponseDto(List<CategoryDto> availableCategories, List<SubscriptionDto> subscribedCategories) {
        this.availableCategories = availableCategories;
        this.subscribedCategories = subscribedCategories;
    }

    public List<CategoryDto> getAvailableCategories() {
        return availableCategories;
    }

    public void setAvailableCategories(List<CategoryDto> availableCategories) {
        this.availableCategories = availableCategories;
    }

    public List<SubscriptionDto> getSubscribedCategories() {
        return subscribedCategories;
    }

    public void setSubscribedCategories(List<SubscriptionDto> subscribedCategories) {
        this.subscribedCategories = subscribedCategories;
    }

    @Override
    public String toString() {
        return "AvailableCategoriesResponseDto{" +
                "availableCategories=" + availableCategories +
                ", subscribedCategories=" + subscribedCategories +
                '}';
    }
}
