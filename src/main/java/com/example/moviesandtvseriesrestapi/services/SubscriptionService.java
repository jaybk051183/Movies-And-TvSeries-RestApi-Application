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
        subscription.setCategory(category);

        LocalDate currentDate = LocalDate.now();
        subscription.setStartDate(currentDate);
        subscription.setPaymentDueDate(currentDate.plusMonths(1)); // Voeg een maand toe voor de gratis periode

        return subscriptionRepository.save(subscription);
    }


    // Begin een transactie (@Transactional: alle databasebewerkingen binnen de functie maken deel uit van een enkele transactie. Als er ergens een fout optreedt, worden alle databasebewerkingen teruggedraaid.)
    @Transactional
    public Subscription subscribeToCategory(String email, Long categoryId) {
        //VIND een User OBJECT met behulp van de gegeven email uit de userRepository:
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User not found."));
        //VIND een Category OBJECT met behulp van de gegeven categoryId uit de categoryRepository:
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found."));
       //CONTROLEER of er al een abonnement bestaat voor de gevonden User en Category:
        if (subscriptionRepository.findByUserAndCategory(user, category).isPresent()) {
            throw new SubscriptionAlreadyExistsException("You are already subscribed to this category.");
        }

        //HAAL de beschikbare inhoud op voor de gevonden categorie en sla dit op als initialRemainingContent:
        Integer initialRemainingContent = category.getAvailableContent();

        //CREEER een nieuw Subscription OBJECT:
         //- STEL de user in als de gevonden user.
         // - STEL de category in als de gevonden category.
         // - STEL de startdatum in als de huidige datum.
         // - STEL de overgebleven inhoud in als initialRemainingContent.
        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setCategory(category);
        subscription.setStartDate(LocalDate.now());
        subscription.setRemainingContent(initialRemainingContent);

        //SLA het Subscription OBJECT op in de subscriptionRepository en RETOURNEER het opgeslagen Subscription OBJECT:
        return subscriptionRepository.save(subscription);
    }

    //VRAAG de subscriptionRepository OM alle Subscription OBJECTEN te vinden geassocieerd met de gegeven user
    public List<SubscriptionDto> findByUser(User user) {
        //VOOR elk gevonden Subscription OBJECT: CONVERTEER het Subscription OBJECT naar een SubscriptionDto OBJECT met behulp van de convertToDto functie:
        return subscriptionRepository.findByUser(user).stream()
                .map(this::convertToDto)
                //VERZAMEL alle geconverteerde SubscriptionDto OBJECTEN in een lijst:
                .collect(Collectors.toList());
    }


    public List<AvailableCategoryDto> getAllAvailableCategories() {
        //HAAL alle Category OBJECTEN uit de categoryRepository en sla ze op in een lijst genaamd categories:
        List<Category> categories = categoryRepository.findAll();

        //TRANSFORMEER elke Category in de categories lijst naar een AvailableCategoryDto door het te 'mappen' met behulp van de functie convertToAvailableCategoryDto:
        return categories.stream()
                .map(this::convertToAvailableCategoryDto)
                .collect(Collectors.toList());
    }


    public List<SubscribedCategoryDto> getSubscribedCategoriesForUser(String username) {
        //Zoek de User OBJECT via de userRepository met het gegeven username (e-mailadres) als zoekcriterium.
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        //HAAL alle Subscription OBJECTEN op voor de gevonden user vanuit subscriptionRepository en sla deze op in een lijst genaamd subscription
        List<Subscription> subscriptions = subscriptionRepository.findByUser(user);

        //TRANSFORMEER elke Subscription in de subscriptions lijst naar een SubscribedCategoryDto door het te 'mappen' met behulp van de functie convertToSubscribedCategoryDto
        return subscriptions.stream()
                .map(this::convertToSubscribedCategoryDto)
                .collect(Collectors.toList());
    }


    public void contentViewed(User user, Category category) {
        //Vindt de Subscription OBJECT voor de gegeven user en category via subscriptionRepository.
        Subscription subscription = subscriptionRepository.findByUserAndCategory(user, category)
                .orElseThrow(() -> new SubscriptionNotFoundException("No subscription found for user and category."));

        //CONTROLEER of de remainingContent van de gevonden Subscription groter is dan 0:
        if(subscription.getRemainingContent() > 0) {
            //VERMINDER de waarde van remainingContent met 1 voor de Subscription
            subscription.setRemainingContent(subscription.getRemainingContent() - 1);
            //SLA de bijgewerkte Subscription op in de subscriptionRepository:
            subscriptionRepository.save(subscription);
        } else {
            throw new RuntimeException("No remaining content for this subscription.");
        }

        //MAAK een nieuw ContentView OBJECT:
       // - STEL de Subscription van het ContentView OBJECT in op de gevonden Subscription.
       // - STEL de viewDate van het ContentView OBJECT in op de huidige datum.
       // - SLA het nieuwe ContentView OBJECT op in contentViewRepository.
        ContentView contentView = new ContentView();
        contentView.setSubscription(subscription);
        contentView.setViewDate(LocalDate.now());
        contentViewRepository.save(contentView);
    }


    //Functionaliteit: scheduler om de betalingsdata bij te houden:
    //ELKE DAG om middernacht (aangegeven door de "cron" expressie "0 0 0 * * ?"):
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkSubscriptionsForPayment() {
        // HAAL een lijst op van alle Subscription OBJECTEN die vandaag betaling verschuldigd zijn vanuit de subscriptionRepository:
        List<Subscription> subscriptionsDueForPayment = subscriptionRepository.findAllByPaymentDueDate(LocalDate.now());

        //VOOR ELKE Subscription in de opgehaalde lijst: VIND de bijbehorende User, genaamd subscriber, van de Subscription.
        for (Subscription subscription : subscriptionsDueForPayment) {
            User subscriber = subscription.getUser();

            //CONTROLEER of er voor deze Subscription nog geen herinnering is verzonden:
            if (!subscription.isReminderSent()) {
                //ALS er geen herinnering is verzonden:
               // - STUUR een betalingsherinnering e-mail naar de subscriber via emailService.
                //  - ZET isReminderSent van Subscription op true.
                //- SLA de bijgewerkte Subscription op in de subscriptionRepository.
                emailService.sendPaymentReminder(subscriber.getEmail(), subscription.getPaymentDueDate());
                subscription.setReminderSent(true);
                subscriptionRepository.save(subscription);
            } else {
                        // - ANDERS (er is al een herinnering verzonden):
                        //  - ZET de Subscription op inactief (setActive(false)).
                       //   - STUUR een abonnementsannulering e-mail naar de subscriber via emailService.
                      //  - SLA de bijgewerkte Subscription op in de subscriptionRepository.
                subscription.setActive(false);
                emailService.sendSubscriptionCancelled(subscriber.getEmail());
                subscriptionRepository.save(subscription);
            }
        }
    }


    // Begin een transactie (@Transactional: alle databasebewerkingen binnen de functie maken deel uit van een enkele transactie. Als er ergens een fout optreedt, worden alle databasebewerkingen teruggedraaid.)
    @Transactional
    public SubscriptionResponseDto shareSubscription(ShareSubscriptionRequestDto request) {

        // Zoek de sharer op basis van het e-mailadres uit het verzoek.
        User sharer = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("Sharer not found."));
        // Zoek de ontvanger op basis van het e-mailadres uit het verzoek.
        User recipient = userRepository.findByEmail(request.getCustomer())
                .orElseThrow(() -> new CustomException("Receiver not found."));
        // Zoek de categorie op basis van de naam van de ingeschreven categorie uit het verzoek.
        Category category = categoryRepository.findByName(request.getSubscribedCategory())
                .orElseThrow(() -> new CustomException("Categorie not found."));
        // Zoek het abonnement van de sharer voor de gegeven categorie.
        Subscription sharerSubscription = subscriptionRepository.findByUserAndCategory(sharer, category)
                .orElseThrow(() -> new CustomException("Subscription not found for sharer."));

        // Als het abonnement van de sharer niet bestaat, retourneer een response.
        if (sharerSubscription == null) {
            return new SubscriptionResponseDto("Login failed", "No subscription for this category.");
        }

        // Halveer het overgebleven content van de sharer's abonnement.
        sharerSubscription.setRemainingContent(sharerSubscription.getRemainingContent() / 2);
        //Sla de gewijzigde sharer's abonnement op in de database
        subscriptionRepository.save(sharerSubscription);

        // Maak een nieuw abonnement voor de ontvanger.
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
