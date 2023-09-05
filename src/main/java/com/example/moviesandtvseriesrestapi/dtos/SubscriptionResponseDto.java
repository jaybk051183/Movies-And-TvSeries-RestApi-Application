package com.example.moviesandtvseriesrestapi.dtos;

import java.time.LocalDate;

public class SubscriptionResponseDto {

    private String status;

    private String message;

    private LocalDate startDate;

    private LocalDate paymentDueDate;

    public SubscriptionResponseDto() {}

    public SubscriptionResponseDto(String status, String message, LocalDate startDate, LocalDate paymentDueDate) {
        this.status = status;
        this.message = message;
        this.startDate = startDate;
        this.paymentDueDate = paymentDueDate;
    }

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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getPaymentDueDate() {
        return paymentDueDate;
    }

    public void setPaymentDueDate(LocalDate paymentDueDate) {
        this.paymentDueDate = paymentDueDate;
    }

    @Override
    public String toString() {
        return "SubscriptionResponseDto{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}