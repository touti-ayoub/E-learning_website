package tn.esprit.microservice2.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.ICourseRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.repo.UserRepository;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private ISubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ICourseRepository courseRepository;

    // Create
    public Subscription createSubscription(Long userId, Long planId, Subscription subscription) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        subscription.setUser(user);
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        return subscriptionRepository.save(subscription);
    }

    // Read (Get by ID)
    public Optional<Subscription> getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id);
    }

    // Read (Get All)
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    // Update
    public Subscription updateSubscription(Long id, Subscription updatedSubscription) {
        return subscriptionRepository.findById(id)
                .map(subscription -> {
                    subscription.setStartDate(updatedSubscription.getStartDate());
                    subscription.setEndDate(updatedSubscription.getEndDate());
                    subscription.setStatus(updatedSubscription.getStatus());
                    subscription.setAutoRenew(updatedSubscription.isAutoRenew());
                    subscription.setUpdatedAt(LocalDateTime.now());
                    return subscriptionRepository.save(subscription);
                })
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));
    }

    // Delete
    public void deleteSubscription(Long id) {
        subscriptionRepository.deleteById(id);
    }


    // Créer un abonnement
    @Transactional
    public Subscription createSubscription(Long userId, Long courseId, PaymentType paymentType,int insattlement) {
        User user = userRepository.findById(userId).get();
        Course course = courseRepository.findById(courseId).get();

        // Rechercher l'utilisateur et le cours
        Subscription subscription = new Subscription();
        subscription.setUser(user);  // Lien avec User
        subscription.setCourse(course);  // Lien avec Course
        subscription.setPaymentType(paymentType);
        subscription.setStatus(SubscriptionStatus.ACTIVE);  // Abonnement par défaut actif

        subscription = subscriptionRepository.save(subscription);

        // Créer un paiement en fonction du type
        paymentService.createPayment(subscription.getId(), paymentType,insattlement);
        return subscription;
    }

    // Mettre à jour l'abonnement (par exemple, renouvellement)
    @Transactional
    public Subscription updateSubscription(Long subscriptionId, SubscriptionStatus status) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.setStatus(status);
            return subscriptionRepository.save(subscription);
        }
        throw new RuntimeException("Subscription not found");
    }

    // Annuler un abonnement
    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);
        if (subscriptionOpt.isPresent()) {
            Subscription subscription = subscriptionOpt.get();
            subscription.setStatus(SubscriptionStatus.CANCELED);
            subscriptionRepository.save(subscription);
        } else {
            throw new RuntimeException("Subscription not found");
        }
    }
}