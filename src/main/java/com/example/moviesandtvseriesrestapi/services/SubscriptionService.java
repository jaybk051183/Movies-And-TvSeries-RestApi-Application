package com.example.moviesandtvseriesrestapi.services;

import com.example.moviesandtvseriesrestapi.dtos.*;
import com.example.moviesandtvseriesrestapi.exceptions.*;
import com.example.moviesandtvseriesrestapi.models.Category;
import com.example.moviesandtvseriesrestapi.models.ContentView;
import com.example.moviesandtvseriesrestapi.models.Subscription;
import com.example.moviesandtvseriesrestapi.models.User;
import com.example.moviesandtvseriesrestapi.repositories.CategoryRepository;
import com.example.moviesandtvseriesrestapi.repositories.ContentViewRepository;
import com.example.moviesandtvseriesrestapi.repositories.SubscriptionRepository;
import com.example.moviesandtvseriesrestapi.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Optional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriptionService {

    private UserRepository userRepository;

    private CategoryRepository categoryRepository;

    private SubscriptionRepository subscriptionRepository;

    private ContentViewRepository contentViewRepository;

    private EmailService emailService;

    public SubscriptionService(UserRepository userRepository, CategoryRepository categoryRepository, SubscriptionRepository subscriptionRepository, ContentViewRepository contentViewRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.contentViewRepository = contentViewRepository;
        this.emailService = emailService;
    }

    public Optional<Subscription> findByUserAndCategory(User user, Category category) {
        return subscriptionRepository.findByUserAndCategory(user, category);
    }

    public Subscription createSubscription(User user, Category category) {
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setRemainingContent(category.getAvailableContent());

        LocalDate currentDate = LocalDate.now();
        subscription.setStartDate(currentDate);
        subscription.setPaymentDueDate(currentDate.plusMonths(1));

        return subscriptionRepository.save(subscription);
    }

    @Transactional
    public Subscription subscribeToCategory(String email, Long categoryId) {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found."));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));

        if (subscriptionRepository.findByUserAndCategory(user, category).isPresent()) {
            throw new SubscriptionAlreadyExistsException("You are already subscribed to this category.");
        }

        Integer initialRemainingContent = category.getAvailableContent();

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setCategory(category);
        subscription.setStartDate(LocalDate.now());
        subscription.setPaymentDueDate(LocalDate.now().plusMonths(1));
        subscription.setRemainingContent(initialRemainingContent);

        return subscriptionRepository.save(subscription);
    }

    public List<SubscriptionDto> findByUser(User user) {
        return subscriptionRepository.findByUser(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public List<AvailableCategoryDto> getAllAvailableCategories() {

        List<Category> categories = categoryRepository.findAll();

        return categories.stream()
                .map(this::convertToAvailableCategoryDto)
                .collect(Collectors.toList());
    }

    public List<SubscribedCategoryDto> getSubscribedCategoriesForUser(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);

        return subscriptions.stream()
                .map(this::convertToSubscribedCategoryDto)
                .collect(Collectors.toList());
    }

    public void contentViewed(User user, Category category) {

        Subscription subscription = subscriptionRepository.findByUserAndCategory(user, category)
                .orElseThrow(() -> new SubscriptionNotFoundException("No subscription found for user and category."));

        if(subscription.getRemainingContent() > 0) {
            subscription.setRemainingContent(subscription.getRemainingContent() - 1);
            subscriptionRepository.save(subscription);

        } else {
            throw new RuntimeException("No remaining content for this subscription.");
        }

        ContentView contentView = new ContentView();
        contentView.setSubscription(subscription);
        contentView.setViewDate(LocalDate.now());

        contentViewRepository.save(contentView);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkSubscriptionsForPayment() {

        List<Subscription> subscriptionsDueForPayment = subscriptionRepository.findAllByPaymentDueDate(LocalDate.now());

        for (Subscription subscription : subscriptionsDueForPayment) {
            User subscriber = subscription.getUser();

            if (!subscription.isReminderSent()) {
                emailService.sendPaymentReminder(subscriber.getEmail(), subscription.getPaymentDueDate());
                subscription.setReminderSent(true);
                subscriptionRepository.save(subscription);
            } else {
                subscription.setActive(false);
                emailService.sendSubscriptionCancelled(subscriber.getEmail());
                subscriptionRepository.save(subscription);
            }
        }
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void resetMonthlyContentLimit() {
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        for (Subscription subscription : allSubscriptions) {
            Category category = subscription.getCategory();
            subscription.setRemainingContent(category.getAvailableContent());
            subscriptionRepository.save(subscription);
        }
    }

    @Transactional
    public SubscriptionResponseDto shareSubscription(ShareSubscriptionRequestDto request) {

        User sharer = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Sharer not found."));

        User recipient = userRepository.findByEmail(request.getCustomer())
                .orElseThrow(() -> new CustomException("Receiver not found."));

        Category category = categoryRepository.findByName(request.getSubscribedCategory())
                .orElseThrow(() -> new CustomException("Categorie not found."));

        Subscription sharerSubscription = subscriptionRepository.findByUserAndCategory(sharer, category)
                .orElseThrow(() -> new CustomException("Subscription not found for sharer."));

        if (sharerSubscription == null) {
            return new SubscriptionResponseDto("Login failed", "No subscription for this category.");
        }

        sharerSubscription.setRemainingContent(sharerSubscription.getRemainingContent() / 2);

        subscriptionRepository.save(sharerSubscription);

        Subscription recipientSubscription = new Subscription();
        recipientSubscription.setUser(recipient);
        recipientSubscription.setCategory(category);
        recipientSubscription.setRemainingContent(sharerSubscription.getRemainingContent());
        LocalDate currentDate = LocalDate.now();
        recipientSubscription.setStartDate(currentDate);
        subscriptionRepository.save(recipientSubscription);

        return new SubscriptionResponseDto("Login successful", "Subscription successfully shared!");
    }

    private int getCurrentMonthContentCountForSubscription(Subscription subscription) {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        List<ContentView> viewsThisMonth = contentViewRepository.findBySubscriptionAndViewDateBetween(subscription, startOfMonth, now);
        return viewsThisMonth.size();
    }

    private AvailableCategoryDto convertToAvailableCategoryDto(Category category) {
        AvailableCategoryDto dto = new AvailableCategoryDto();
        dto.setName(category.getName());
        dto.setAvailableContent(category.getAvailableContent());
        dto.setPrice(category.getPrice());
        return dto;
    }

    private SubscribedCategoryDto convertToSubscribedCategoryDto(Subscription subscription) {
        SubscribedCategoryDto dto = new SubscribedCategoryDto();
        dto.setName(subscription.getCategory().getName());
        dto.setRemainingContent(subscription.getRemainingContent());
        dto.setPrice(subscription.getCategory().getPrice());
        dto.setStartDate(subscription.getStartDate());
        return dto;
    }

    private SubscriptionDto convertToDto(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();

        Category category = subscription.getCategory();
        if (category != null) {
            dto.setCategoryId(category.getId());
            dto.setCategoryName(category.getName());
        }

        LocalDate startDate = subscription.getStartDate();
        if (startDate != null) {
            dto.setStartDate(startDate);
        }

        dto.setRemainingContent(subscription.getRemainingContent());
        dto.setCurrentMonthContentCount(getCurrentMonthContentCountForSubscription(subscription));

        ContentView latestView = contentViewRepository.findTopBySubscriptionOrderByViewDateDesc(subscription);
        if (latestView != null) {
            dto.setLastAccessedDate(latestView.getViewDate());
        }
        return dto;
    }
}
