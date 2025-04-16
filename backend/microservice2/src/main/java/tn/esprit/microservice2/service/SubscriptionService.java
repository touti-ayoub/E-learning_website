package tn.esprit.microservice2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice2.DTO.SubCreatingRequest;
import tn.esprit.microservice2.DTO.SubscriptionDTO;
import tn.esprit.microservice2.DTO.UserDTO;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.ICourseRepository;
import tn.esprit.microservice2.repo.ISubscriptionRepository;
import tn.esprit.microservice2.repo.UserRepository;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    @Autowired
    private ISubscriptionRepository subscriptionRepository;
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  PaymentService paymentService;
    @Autowired
    private  ICourseRepository courseRepository;

    @Transactional
    public SubscriptionDTO createSubscription(SubCreatingRequest subRequest) {
        log.debug("Creating subscription for user: {} and course: {}", subRequest.getUserId(), subRequest.getCourseId());

        try {
            User user = userRepository.findById(subRequest.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + subRequest.getUserId()));

            Course course = courseRepository.findById(subRequest.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + subRequest.getCourseId()));

            boolean hasActiveSubscription = subscriptionRepository
                    .findByUserAndCourseAndStatus(user, course, SubscriptionStatus.ACTIVE)
                    .isPresent();

            if (hasActiveSubscription) {
                throw new RuntimeException("User already has an active subscription for this course");
            }

            LocalDateTime now = LocalDateTime.now();

            Subscription subscription = new Subscription();
            subscription.setUser(user);
            subscription.setCourse(course);
            subscription.setPaymentType(subRequest.getPaymentType());
            subscription.setStatus(SubscriptionStatus.PENDING);
            subscription.setStartDate(now);
            subscription.setEndDate(now.plusMonths(course.getDurationInMonths()));
            subscription.setAutoRenew(subRequest.isAutoRenew());
            subscription.setCreatedAt(now);
            subscription.setUpdatedAt(now);

            subscription = subscriptionRepository.save(subscription);

            if (PaymentType.FULL.equals(subRequest.getPaymentType())) {
                paymentService.createFullPayment(subscription);
            } else if (subRequest.getInstallments() != null) {
                BigDecimal totalAmount = subscription.getCourse().getPrice();
                paymentService.createPaymentWithInstallments(subscription, totalAmount, subRequest.getInstallments());
            }

            return SubscriptionDTO.fromEntity(subscription);
        } catch (Exception e) {
            log.error("Failed to create subscription", e);
            throw new RuntimeException("Failed to create subscription: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getUserSubscriptions(Long userId) {
        log.debug("Fetching subscriptions for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        return subscriptionRepository.findByUser(user).stream()
                .map(SubscriptionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDTO> getAllSubscriptions() {
        log.debug("Fetching all subscriptions");

        return subscriptionRepository.findAll().stream()
                .map(SubscriptionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public SubscriptionDTO updateSubscriptionStatus(Long subscriptionId, SubscriptionStatus status) {
        log.debug("Updating subscription {} status to: {}", subscriptionId, status);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + subscriptionId));

        subscription.setStatus(status);
        subscription.setUpdatedAt(LocalDateTime.now());

        if (status == SubscriptionStatus.CANCELED) {
            subscription.setEndDate(LocalDateTime.now());
            subscription.setAutoRenew(false);
        }

        subscription = subscriptionRepository.save(subscription);
        return SubscriptionDTO.fromEntity(subscription);
    }

    @Transactional
    public SubscriptionDTO updateSubscription(Long id, Subscription updatedSubscription) {
        log.debug("Updating subscription: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        subscription.setStartDate(updatedSubscription.getStartDate());
        subscription.setEndDate(updatedSubscription.getEndDate());
        subscription.setStatus(updatedSubscription.getStatus());
        subscription.setAutoRenew(updatedSubscription.isAutoRenew());
        subscription.setPaymentType(updatedSubscription.getPaymentType());
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        return SubscriptionDTO.fromEntity(subscription);
    }

    @Transactional
    public void cancelSubscription(Long subscriptionId) {
        log.debug("Cancelling subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + subscriptionId));

        if (subscription.getStatus() == SubscriptionStatus.CANCELED) {
            throw new RuntimeException("Subscription is already cancelled");
        }

        subscription.setStatus(SubscriptionStatus.CANCELED);
        subscription.setEndDate(LocalDateTime.now());
        subscription.setAutoRenew(false);
        subscription.setUpdatedAt(LocalDateTime.now());

        subscriptionRepository.save(subscription);
    }

    @Transactional
    public SubscriptionDTO renewSubscription(Long subscriptionId) {
        log.debug("Renewing subscription: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + subscriptionId));

        if (!subscription.isAutoRenew()) {
            throw new RuntimeException("Auto-renewal is not enabled for this subscription");
        }

        LocalDateTime newEndDate = subscription.getEndDate()
                .plusMonths(subscription.getCourse().getDurationInMonths());
        subscription.setEndDate(newEndDate);
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setUpdatedAt(LocalDateTime.now());

        subscription = subscriptionRepository.save(subscription);
        return SubscriptionDTO.fromEntity(subscription);
    }

    @Transactional(readOnly = true)
    public boolean isSubscriptionActive(Long userId, Long courseId) {
        log.debug("Checking active subscription for user: {} and course: {}", userId, courseId);

        return subscriptionRepository
                .findByUserIdAndCourseIdAndStatus(userId, courseId, SubscriptionStatus.ACTIVE)
                .isPresent();
    }

    @Transactional
    public void deleteSubscription(Long id) {
        log.debug("Deleting subscription: {}", id);

        Subscription subscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

        if (subscription.getStatus() != SubscriptionStatus.CANCELED
                && subscription.getStatus() != SubscriptionStatus.EXPIRED) {
            throw new RuntimeException("Can only delete cancelled or expired subscriptions");
        }

        subscriptionRepository.deleteById(id);
        log.debug("Subscription deleted successfully: {}", id);
    }
    @Transactional(readOnly = true)
    public SubscriptionDTO getSubscriptionById(Long id) {
        log.debug("Fetching subscription with id: {}", id);

        try {
            Subscription subscription = subscriptionRepository.findByIdWithDetails(id)
                    .orElseThrow(() -> new RuntimeException("Subscription not found with id: " + id));

            SubscriptionDTO dto = SubscriptionDTO.fromEntity(subscription);

            // Verify all required fields are set
            log.debug("Subscription details - ID: {}, Status: {}, PaymentType: {}, " +
                            "StartDate: {}, EndDate: {}, User: {}, Course: {}",
                    dto.getId(), dto.getStatus(), dto.getPaymentType(),
                    dto.getStartDate(), dto.getEndDate(),
                    dto.getUser() != null ? dto.getUser().getId() : null,
                    dto.getCourse() != null ? dto.getCourse().getId() : null);

            return dto;
        } catch (Exception e) {
            log.error("Error fetching subscription with id: {}", id, e);
            throw new RuntimeException("Error fetching subscription: " + e.getMessage(), e);
        }
    }

    @Transactional
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}