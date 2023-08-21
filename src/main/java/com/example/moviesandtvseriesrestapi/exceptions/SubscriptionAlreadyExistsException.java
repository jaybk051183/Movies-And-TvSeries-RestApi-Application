package com.example.moviesandtvseriesrestapi.exceptions;

public class SubscriptionAlreadyExistsException extends RuntimeException {
    public SubscriptionAlreadyExistsException() {
        super("Subscription already exists.");
    }

    public SubscriptionAlreadyExistsException(String message) {
        super(message);
    }
}
