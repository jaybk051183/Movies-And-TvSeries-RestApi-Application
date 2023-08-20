package com.example.moviesandtvseriesrestapi.models;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "content_views")
public class ContentView {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "view_date")
    private LocalDate viewDate;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    public ContentView(Long id, Subscription subscription, LocalDate viewDate) {
        this.id = id;
        this.subscription = subscription;
        this.viewDate = viewDate;
    }

    public ContentView() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public LocalDate getViewDate() {
        return viewDate;
    }

    public void setViewDate(LocalDate viewDate) {
        this.viewDate = viewDate;
    }
}
