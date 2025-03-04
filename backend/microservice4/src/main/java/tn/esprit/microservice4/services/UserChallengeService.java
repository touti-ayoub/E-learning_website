package tn.esprit.microservice4.services;

import tn.esprit.microservice4.entities.UserChallenge;
import tn.esprit.microservice4.entities.User;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.entities.Badge;
import tn.esprit.microservice4.repositories.UserChallengeRepository;
import tn.esprit.microservice4.repositories.UserRepository;
import tn.esprit.microservice4.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserChallengeService {

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private PointService pointService;

    @Autowired
    private BadgeService badgeService;

    // Method for joining a challenge
    public UserChallenge joinChallenge(Long userId, Long challengeId) {
        // Fetch the User and Challenge entities using their IDs
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge with ID " + challengeId + " does not exist"));

        // Check if the user has already joined this challenge
        if (userChallengeRepository.existsByUserAndChallenge(user, challenge)) {
            throw new IllegalArgumentException("User has already joined this challenge.");
        }

        // Create a new UserChallenge and set its properties
        UserChallenge userChallenge = new UserChallenge();
        userChallenge.setUser(user);
        userChallenge.setChallenge(challenge);
        userChallenge.setCompleted(false); // Initially, the challenge is not completed

        // Save the new UserChallenge relationship
        return userChallengeRepository.save(userChallenge);
    }

    // Method for completing a challenge
    public void completeChallenge(Long userChallengeId) {
        UserChallenge userChallenge = userChallengeRepository.findById(userChallengeId)
                .orElseThrow(() -> new IllegalArgumentException("UserChallenge with ID " + userChallengeId + " does not exist"));

        userChallenge.setCompleted(true);
        userChallengeRepository.save(userChallenge);

        // Assign points to the user for completing the challenge
        pointService.addPointsToUser(userChallenge.getUser().getId(), userChallenge.getChallenge().getIdChallenge()); // Use getIdChallenge()

        // Create and assign a badge for the challenge completion
        Badge badge = badgeService.createBadgeForChallenge(userChallenge.getChallenge());
        badgeService.assignBadgeToUser(userChallenge.getUser(), badge);
    }

    // Method to get a list of completed challenges for a user
    public List<UserChallenge> getCompletedChallenges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));

        return userChallengeRepository.findByUserAndCompletedTrue(user); // Use the appropriate method for filtering completed challenges
    }

    // Method to get a list of ongoing challenges for a user
    public List<UserChallenge> getOngoingChallenges(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));

        return userChallengeRepository.findByUserAndCompletedFalse(user); // Use the appropriate method for ongoing challenges
    }
}
