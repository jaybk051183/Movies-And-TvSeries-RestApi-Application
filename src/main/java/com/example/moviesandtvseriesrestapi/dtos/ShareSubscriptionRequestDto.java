package com.example.moviesandtvseriesrestapi.dtos;

public class ShareSubscriptionRequestDto {

    private String email;
    private String customer;
    private String subscribedCategory;

    public ShareSubscriptionRequestDto() {}

    public ShareSubscriptionRequestDto(String email, String customer, String subscribedCategory) {
        this.email = email;
        this.customer = customer;
        this.subscribedCategory = subscribedCategory;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getSubscribedCategory() {
        return subscribedCategory;
    }

    public void setSubscribedCategory(String subscribedCategory) {
        this.subscribedCategory = subscribedCategory;
    }

    @Override
    public String toString() {
        return "ShareSubscriptionRequestDto{" +
                "email='" + email + '\'' +
                ", customer='" + customer + '\'' +
                ", subscribedCategory='" + subscribedCategory + '\'' +
                '}';
    }
}