package com.example.moviesandtvseriesrestapi.models;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "payment_due_date")
    private LocalDate paymentDueDate;

    @Column(name = "remaining_content", nullable = false)
    private Integer remainingContent;

    @Column(name = "reminder_sent", nullable = false)
    private boolean reminderSent = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ContentView> contentViews;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public Subscription() {}

    public Subscription(Long id, LocalDate startDate, LocalDate paymentDueDate, Integer remainingContent, boolean reminderSent, boolean active, User user, Category category) {
        this.id = id;
        this.startDate = startDate;
        this.paymentDueDate = paymentDueDate;
        this.remainingContent = remainingContent;
        this.reminderSent = reminderSent;
        this.active = active;
        this.user = user;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public Integer getRemainingContent() {
        return remainingContent;
    }

    public void setRemainingContent(Integer remainingContent) {
        this.remainingContent = remainingContent;
    }

    public boolean isReminderSent() {
        return reminderSent;
    }

    public void setReminderSent(boolean reminderSent) {
        this.reminderSent = reminderSent;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ContentView> getContentViews() {
        return contentViews;
    }

    public void setContentViews(List<ContentView> contentViews) {
        this.contentViews = contentViews;
    }

    public void addContentView(ContentView contentView) {
        contentViews.add(contentView);
        contentView.setSubscription(this);
    }

    public void removeContentView(ContentView contentView) {
        contentViews.remove(contentView);
        contentView.setSubscription(null);
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", user=" + user +
                ", category=" + category +
                ", startDate=" + startDate +
                ", remainingContent=" + remainingContent +
                '}';
    }
}
