package com.example.moviesandtvseriesrestapi.controllers;

import com.example.moviesandtvseriesrestapi.dtos.AvailableCategoriesResponseDto;
import com.example.moviesandtvseriesrestapi.dtos.ContentViewRequestDto;
import com.example.moviesandtvseriesrestapi.dtos.SubscriptionRequestDto;
import com.example.moviesandtvseriesrestapi.dtos.SubscriptionResponseDto;
import com.example.moviesandtvseriesrestapi.exceptions.CategoryNotFoundException;
import com.example.moviesandtvseriesrestapi.exceptions.UserNotFoundException;
import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.models.Subscription;
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

    //Constructor-based dependency injection:
    public CategoryController(UserService userService, CategoryService categoryService, SubscriptionService subscriptionService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.subscriptionService = subscriptionService;
    }

    //AvailableAndSubscribedCategoriesResponseDto zou een betere benaming zijn
    @GetMapping
    public AvailableCategoriesResponseDto getCategories(@RequestHeader String username) {
        //ZOEK de gebruiker met de gegeven username (e-mailadres) via userService:
        User user = userService.findByEmail(username).orElseThrow(() -> new UserNotFoundException("User not found."));
                //INITIEER een nieuw Dto object:
        AvailableCategoriesResponseDto response = new AvailableCategoriesResponseDto();
        //VUL response's availableCategories MET ALLE categorieën verkregen van categoryService:
        response.setAvailableCategories(categoryService.findAll());
        //VUL response's subscribedCategories MET categorieën WAAROP de gevonden gebruiker geabonneerd is, verkregen van subscriptionService:
        response.setSubscribedCategories(subscriptionService.findByUser(user));
        return response;
    }

    //*Per opdracht:  moet availableCategory in de GET van het type Sting zijn in de JSON  ipv Long, dus SubscriptionRequestDto niet correct opgezet!
    @PostMapping("/subscribe")
    public ResponseEntity<SubscriptionResponseDto> subscribeToCategory(@RequestBody SubscriptionRequestDto request) {
        try {
            //INITIEER een Subscription OBJECT (response dto) door te abonneren op een categorie m.b.v. een POST met  email en availableCategory via subscriptionService
            Subscription subscription = subscriptionService.subscribeToCategory(request.getEmail(), request.getAvailableCategory());
            return ResponseEntity.ok(new SubscriptionResponseDto("Login successful.", "Successfully subscribed to " + subscription.getCategory().getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new SubscriptionResponseDto("Login failed.", e.getMessage()));
        }
    }

    @PostMapping("/view")
    public ResponseEntity<String> viewContent(@RequestBody ContentViewRequestDto request) {
        try {
            //VIND een User OBJECT op basis van de email via de userService:
            User user = userService.findByEmail(request.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found."));
            //VIND een Category OBJECT op basis van de categoryName via categoryService
            Category category = categoryService.findByName(request.getCategoryName()).orElseThrow(() -> new CategoryNotFoundException("Category not found."));

            //INFORMEER de subscriptionService dat de inhoud is bekeken met de gevonden gebruiker en categorie
            subscriptionService.contentViewed(user, category);

            return ResponseEntity.ok("Content viewed successfully.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

