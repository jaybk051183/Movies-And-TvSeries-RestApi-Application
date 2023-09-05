package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.*;
import com.example.moviesandtvseriesrestapi.exceptions.CategoryNotFoundException;
import com.example.moviesandtvseriesrestapi.exceptions.SubscriptionNotFoundException;
import com.example.moviesandtvseriesrestapi.exceptions.UserNotFoundException;
import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.models.Subscription;
import com.example.moviesandtvseriesrestapi.models.User;
import com.example.moviesandtvseriesrestapi.services.CategoryService;
import com.example.moviesandtvseriesrestapi.services.SubscriptionService;
import com.example.moviesandtvseriesrestapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private SubscriptionService subscriptionService;

    private CategoryService categoryService;

    private UserService userService;

    public SubscriptionController(SubscriptionService subscriptionService, CategoryService categoryService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(@RequestBody SubscriptionRequestDto request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        Category category = categoryService.findByName(request.getCategoryName())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        Subscription subscription = subscriptionService.createSubscription(user, category);

        SubscriptionResponseDto response = new SubscriptionResponseDto();
        response.setStatus("Success");
        response.setMessage("Subscription created successfully with a free month!");
        response.setStartDate(subscription.getStartDate());
        response.setPaymentDueDate(subscription.getPaymentDueDate());

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getCategoriesForUser(@RequestParam String username) {

        List<AvailableCategoryDto> availableCategories = subscriptionService.getAllAvailableCategories();
        List<SubscribedCategoryDto> subscribedCategories = subscriptionService.getSubscribedCategoriesForUser(username);

        Map<String, Object> response = new HashMap<>();
        response.put("availableCategories", availableCategories);
        response.put("subscribedCategories", subscribedCategories);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/mark-as-viewed")
    public ResponseEntity<String> markAsViewed(@RequestParam String email, @RequestParam String categoryName) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        Category category = categoryService.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
        subscriptionService.contentViewed(user, category);
        return ResponseEntity.ok("Content marked as viewed successfully.");
    }

    @GetMapping("/payment-due-date")
    public ResponseEntity<SubscriptionResponseDto> checkPaymentDueDate(@RequestParam String email, @RequestParam String categoryName) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        Category category = categoryService.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        Subscription subscription = subscriptionService.findByUserAndCategory(user, category)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found."));

        SubscriptionResponseDto response = new SubscriptionResponseDto();
        response.setStatus("Success");
        response.setMessage("Subscription payment due date retrieved successfully!");
        response.setStartDate(subscription.getStartDate());
        response.setPaymentDueDate(subscription.getPaymentDueDate());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/remaining-content")
    public ResponseEntity<Integer> getRemainingContent(@RequestParam String email, @RequestParam String categoryName) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        Category category = categoryService.findByName(categoryName)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
        Subscription subscription = subscriptionService.findByUserAndCategory(user, category)
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found."));
        return ResponseEntity.ok(subscription.getRemainingContent());
    }

    @PostMapping("/share")
    public ResponseEntity<SubscriptionResponseDto> shareSubscription(@RequestBody ShareSubscriptionRequestDto request) {
        return ResponseEntity.ok(subscriptionService.shareSubscription(request));
    }
}
