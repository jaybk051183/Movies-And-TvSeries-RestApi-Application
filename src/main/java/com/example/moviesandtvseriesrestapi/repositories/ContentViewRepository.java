package com.example.moviesandtvseriesrestapi.repositories;

import com.example.moviesandtvseriesrestapi.models.ContentView;
import com.example.moviesandtvseriesrestapi.models.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ContentViewRepository extends JpaRepository<ContentView, Long> {
    List<ContentView> findBySubscriptionAndViewDateBetween(Subscription subscription, LocalDate startDate, LocalDate endDate);
    ContentView findTopBySubscriptionOrderByViewDateDesc(Subscription subscription);
}

