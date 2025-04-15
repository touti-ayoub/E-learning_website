package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.UserChallenge;
import tn.esprit.microservice4.services.UserChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mic4/userChallenges")
public class UserChallengeController {

    @Autowired
    private UserChallengeService userChallengeService;

    // Rejoindre un challenge
    @PostMapping("/join")
    public ResponseEntity<UserChallenge> joinChallenge(@RequestParam Long userId, @RequestParam Long challengeId) {
        UserChallenge userChallenge = userChallengeService.joinChallenge(userId, challengeId);
        return new ResponseEntity<>(userChallenge, HttpStatus.CREATED);
    }

    // Compléter un challenge
    @PostMapping("/complete")
    public ResponseEntity<Void> completeChallenge(@RequestParam Long userChallengeId) {
        userChallengeService.completeChallenge(userChallengeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Voir les challenges complétés par un utilisateur
    @GetMapping("/completed/{userId}")
    public ResponseEntity<List<UserChallenge>> getCompletedChallenges(@PathVariable Long userId) {
        List<UserChallenge> completedChallenges = userChallengeService.getCompletedChallenges(userId);
        return new ResponseEntity<>(completedChallenges, HttpStatus.OK);
    }

    // Voir les challenges en cours pour un utilisateur
    @GetMapping("/ongoing/{userId}")
    public ResponseEntity<List<UserChallenge>> getOngoingChallenges(@PathVariable Long userId) {
        List<UserChallenge> ongoingChallenges = userChallengeService.getOngoingChallenges(userId);
        return new ResponseEntity<>(ongoingChallenges, HttpStatus.OK);
    }
}