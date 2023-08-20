package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.AvailableCategoryDto;
import com.example.moviesandtvseriesrestapi.dtos.ShareSubscriptionRequestDto;
import com.example.moviesandtvseriesrestapi.dtos.SubscribedCategoryDto;
import com.example.moviesandtvseriesrestapi.dtos.SubscriptionResponseDto;
import com.example.moviesandtvseriesrestapi.services.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {

    private SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
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

    @PostMapping("/share")
    public ResponseEntity<SubscriptionResponseDto> shareSubscription(@RequestBody ShareSubscriptionRequestDto request) {
        return ResponseEntity.ok(subscriptionService.shareSubscription(request));
    }
}
