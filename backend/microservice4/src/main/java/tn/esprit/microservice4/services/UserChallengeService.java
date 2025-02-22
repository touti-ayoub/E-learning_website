package tn.esprit.microservice4.services;

import tn.esprit.microservice4.entities.UserChallenge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.repositories.UserChallengeRepository;

import java.util.List;

@Service
public class UserChallengeService {

    @Autowired
    private UserChallengeRepository userChallengeRepository;

    public List<UserChallenge> getAllUserChallenges() {
        return userChallengeRepository.findAll();
    }

    public UserChallenge getUserChallengeById(Long id) {
        return userChallengeRepository.findById(id).orElseThrow(() -> new RuntimeException("UserChallenge not found"));
    }

    public UserChallenge createUserChallenge(UserChallenge userChallenge) {
        return userChallengeRepository.save(userChallenge);
    }

    public UserChallenge updateUserChallenge(Long id, UserChallenge userChallenge) {
        userChallenge.setId(id);
        return userChallengeRepository.save(userChallenge);
    }

    public void deleteUserChallenge(Long id) {
        userChallengeRepository.deleteById(id);
    }
}
