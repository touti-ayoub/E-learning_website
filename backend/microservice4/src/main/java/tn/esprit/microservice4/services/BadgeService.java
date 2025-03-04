package tn.esprit.microservice4.services;

import tn.esprit.microservice4.entities.Badge;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.entities.User;
import tn.esprit.microservice4.repositories.BadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice4.repositories.UserRepository;

import java.util.List;

@Service
public class BadgeService {

    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private UserRepository userRepository;

    // Get all badges
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    // Get badge by ID
    public Badge getBadgeById(Long id) {
        return badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge not found with ID: " + id));
    }

    // Create a new badge
    @Transactional
    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    // Create a badge for a challenge
    @Transactional
    public Badge createBadgeForChallenge(Challenge challenge) {
        // Example logic to create a badge based on a challenge
        Badge badge = new Badge();
        badge.setName("Completed " + challenge.getName() + " Challenge");
        badge.setDescription("Awarded for completing the " + challenge.getName() + " challenge.");
        // You can add more logic to assign additional properties to the badge

        return badgeRepository.save(badge);
    }

    // Assign a badge to a user
    @Transactional
    public void assignBadgeToUser(User user, Badge badge) {
        // Add the badge to the user's badges set
        user.addBadge(badge);

        // Save the updated user, which will automatically update the relationship in the join table
        userRepository.save(user);     }
    // Update an existing badge
    @Transactional
    public Badge updateBadge(Long id, Badge badge) {
        badge.setIdBadge(id);
        return badgeRepository.save(badge);
    }

    // Delete a badge
    @Transactional
    public void deleteBadge(Long id) {
        badgeRepository.deleteById(id);
    }
}
