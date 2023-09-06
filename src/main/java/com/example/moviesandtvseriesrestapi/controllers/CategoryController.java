package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.AvailableCategoriesResponseDto;
import com.example.moviesandtvseriesrestapi.dtos.ContentViewRequestDto;
import com.example.moviesandtvseriesrestapi.exceptions.CategoryNotFoundException;
import com.example.moviesandtvseriesrestapi.exceptions.UserNotFoundException;
import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.models.User;
import com.example.moviesandtvseriesrestapi.services.CategoryService;
import com.example.moviesandtvseriesrestapi.services.SubscriptionService;
import com.example.moviesandtvseriesrestapi.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private UserService userService;

    private CategoryService categoryService;

    private SubscriptionService subscriptionService;

    public CategoryController(UserService userService, CategoryService categoryService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public AvailableCategoriesResponseDto getCategories(@RequestHeader String username) {

        User user = userService.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User not found."));

        AvailableCategoriesResponseDto response = new AvailableCategoriesResponseDto();
        response.setAvailableCategories(categoryService.findAll());
        response.setSubscribedCategories(subscriptionService.findByUser(user));

        return response;
    }

    @PostMapping("/view")
    public ResponseEntity<String> viewContent(@RequestBody ContentViewRequestDto request) {
        try {
            User user = userService.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found."));
            Category category = categoryService.findByName(request.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException("Category not found."));

            subscriptionService.contentViewed(user, category);

            return ResponseEntity.ok("Content viewed successfully.");

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

