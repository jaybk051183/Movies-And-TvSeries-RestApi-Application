package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.*;
import com.example.moviesandtvseriesrestapi.exceptions.CustomException;
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

    //Constructor-based dependency injection:
    public SubscriptionController(SubscriptionService subscriptionService, CategoryService categoryService, UserService userService) {
        this.subscriptionService = subscriptionService;
        this.categoryService = categoryService;
        this.userService = userService;
    }


    @GetMapping("/payment-due-date")
    public ResponseEntity<SubscriptionResponseDto> checkPaymentDueDate(@RequestParam String email, @RequestParam String categoryName) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found."));
        Category category = categoryService.findByName(categoryName)
                .orElseThrow(() -> new CustomException("Category not found."));

        Subscription subscription = subscriptionService.findByUserAndCategory(user, category)
                .orElseThrow(() -> new CustomException("Subscription not found."));

        SubscriptionResponseDto response = new SubscriptionResponseDto();
        response.setStatus("Success");
        response.setMessage("Subscription payment due date retrieved successfully!");
        response.setStartDate(subscription.getStartDate());
        response.setPaymentDueDate(subscription.getPaymentDueDate());

        return ResponseEntity.ok(response);
    }


    //Haal zowel de beschikbare categorieën als de categorieën waarop een specifieke gebruiker geabonneerd is.
    //Door ResponseEntity<?> (generics) te gebruiken, kan de controller-methode verschillende types van objecten retourneren.
    @GetMapping
    public ResponseEntity<?> getCategoriesForUser(@RequestParam String username) {
        // Haal alle beschikbare categorieën op via de subscriptionService.
        List<AvailableCategoryDto> availableCategories = subscriptionService.getAllAvailableCategories();
        // Haal de categorieën op waarop de gebruiker (geïdentificeerd door username) is geabonneerd.
        List<SubscribedCategoryDto> subscribedCategories = subscriptionService.getSubscribedCategoriesForUser(username);

        // Maak een nieuwe map (=collectie met key/value pairs) om de opgehaalde categorieën op te slaan.
        Map<String, Object> response = new HashMap<>();
        //VOEG beschikbare Categorieën TOE AAN response ONDER SLEUTEL "availableCategories"
        response.put("availableCategories", availableCategories);
        //VOEG geabonneerdeCategorieën TOE AAN response ONDER SLEUTEL "subscribedCategories"
        response.put("subscribedCategories", subscribedCategories);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/create")
    public ResponseEntity<SubscriptionResponseDto> createSubscription(@RequestBody SubscriptionRequestDto request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("User not found."));
        Category category = categoryService.findByName(request.getCategoryName())
                .orElseThrow(() -> new CustomException("Category not found."));

        Subscription subscription = subscriptionService.createSubscription(user, category);

        SubscriptionResponseDto response = new SubscriptionResponseDto();
        response.setStatus("Success");
        response.setMessage("Subscription created successfully with a free month!");
        response.setStartDate(subscription.getStartDate());
        response.setPaymentDueDate(subscription.getPaymentDueDate());

        return ResponseEntity.ok(response);
    }


    @PostMapping("/share")
    public ResponseEntity<SubscriptionResponseDto> shareSubscription(@RequestBody ShareSubscriptionRequestDto request) {
        return ResponseEntity.ok(subscriptionService.shareSubscription(request));
    }
}
