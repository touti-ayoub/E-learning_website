package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.services.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mic4/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    // Récupérer tous les challenges
    @GetMapping
    public ResponseEntity<List<Challenge>> getAllChallenges() {
        List<Challenge> challenges = challengeService.getAllChallenges();
        return new ResponseEntity<>(challenges, HttpStatus.OK);
    }

    // Récupérer un challenge par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Challenge> getChallengeById(@PathVariable Long id) {
        Challenge challenge = challengeService.getChallengeById(id);
        return new ResponseEntity<>(challenge, HttpStatus.OK);
    }

    // Créer un nouveau challenge
    @PostMapping
    public ResponseEntity<Challenge> createChallenge(@RequestBody Challenge challenge) {
        Challenge createdChallenge = challengeService.createChallenge(challenge);
        return new ResponseEntity<>(createdChallenge, HttpStatus.CREATED);
    }

    // Mettre à jour un challenge
    @PutMapping("/{id}")
    public ResponseEntity<Challenge> updateChallenge(@PathVariable Long id, @RequestBody Challenge challengeDetails) {
        Challenge updatedChallenge = challengeService.updateChallenge(id, challengeDetails);
        return new ResponseEntity<>(updatedChallenge, HttpStatus.OK);
    }

    // Supprimer un challenge
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallenge(@PathVariable Long id) {
        challengeService.deleteChallenge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
