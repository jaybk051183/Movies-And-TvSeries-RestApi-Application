package com.example.moviesandtvseriesrestapi.dtos;

import java.time.LocalDate;

public class SubscriptionDto {
    private Long categoryId;

    private String categoryName;

    private LocalDate startDate;

    private Integer remainingContent;

    private Integer currentMonthContentCount;

    private LocalDate lastAccessedDate;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getRemainingContent() {
        return remainingContent;
    }

    public void setRemainingContent(Integer remainingContent) {
        this.remainingContent = remainingContent;
    }

    public Integer getCurrentMonthContentCount() {
        return currentMonthContentCount;
    }

    public void setCurrentMonthContentCount(Integer currentMonthContentCount) {
        this.currentMonthContentCount = currentMonthContentCount;
    }

    public LocalDate getLastAccessedDate() {
        return lastAccessedDate;
    }

    public void setLastAccessedDate(LocalDate lastAccessedDate) {
        this.lastAccessedDate = lastAccessedDate;
    }
}
