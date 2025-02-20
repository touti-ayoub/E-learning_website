package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Challenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice4.repositories.ChallengeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/backoffice/challenges")
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping
    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Challenge getChallengeById(@PathVariable Long id) {
        return challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    @PostMapping
    public Challenge createChallenge(@RequestBody Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    @PutMapping("/{id}")
    public Challenge updateChallenge(@PathVariable Long id, @RequestBody Challenge challenge) {
        challenge.setIdChallenge(id);
        return challengeRepository.save(challenge);
    }

    @DeleteMapping("/{id}")
    public void deleteChallenge(@PathVariable Long id) {
        challengeRepository.deleteById(id);
    }
}
