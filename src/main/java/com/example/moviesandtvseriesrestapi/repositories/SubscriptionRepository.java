package com.example.moviesandtvseriesrestapi.repositories;

import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.models.Subscription;
import com.example.moviesandtvseriesrestapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUser(User user);

    Optional<Subscription> findByUserAndCategory(User user, Category category);

    List<Subscription> findAllByPaymentDueDate(LocalDate date);

    Optional<Subscription> findByUserAndCategoryName(User user, String categoryName);
}

