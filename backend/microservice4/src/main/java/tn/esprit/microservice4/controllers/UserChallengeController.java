package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.UserChallenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice4.repositories.UserChallengeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/backoffice/user-challenges")
public class UserChallengeController {

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    @GetMapping
    public List<UserChallenge> getAllUserChallenges() {
        return userChallengeRepository.findAll();
    }

    @GetMapping("/{id}")
    public UserChallenge getUserChallengeById(@PathVariable Long id) {
        return userChallengeRepository.findById(id).orElseThrow(() -> new RuntimeException("UserChallenge not found"));
    }

    @PostMapping
    public UserChallenge createUserChallenge(@RequestBody UserChallenge userChallenge) {
        return userChallengeRepository.save(userChallenge);
    }

    @PutMapping("/{id}")
    public UserChallenge updateUserChallenge(@PathVariable Long id, @RequestBody UserChallenge userChallenge) {
        userChallenge.setId(id);
        return userChallengeRepository.save(userChallenge);
    }

    @DeleteMapping("/{id}")
    public void deleteUserChallenge(@PathVariable Long id) {
        userChallengeRepository.deleteById(id);
    }
}
