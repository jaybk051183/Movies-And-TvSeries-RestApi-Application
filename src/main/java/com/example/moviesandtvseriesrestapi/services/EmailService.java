package com.example.moviesandtvseriesrestapi.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public void sendPaymentReminder(String toEmail, LocalDate paymentDueDate) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Payment reminder");
        message.setText("Your payment for " + paymentDueDate + " has not been received. Please play the soonest.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending reminder email: " + e.getMessage());
        }
    }

    public void sendSubscriptionCancelled(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Subscription cancelled");
        message.setText("Subscription cancelled due to missing payment.");

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Error sending cancellation email: " + e.getMessage());
        }
    }
}
