package tn.esprit.microservice4.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.repositories.BadgeRepository;
import tn.esprit.microservice4.repositories.ChallengeRepository;

import java.util.List;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private BadgeRepository badgeRepository;

    public Challenge createChallenge(Challenge challenge) {
        return challengeRepository.save(challenge);
    }

    public List<Challenge> getAllChallenges() {
        return challengeRepository.findAll();
    }
    public Challenge getChallengeById(Long id) {
        return challengeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }
    public Challenge updateChallenge(Long id, Challenge challengeDetails) {
        Challenge challenge = challengeRepository.findById(id).orElseThrow(() -> new RuntimeException("Challenge not found"));
        challenge.setName(challengeDetails.getName());
        challenge.setDescription(challengeDetails.getDescription());
        challenge.setRewardPoints(challengeDetails.getRewardPoints());
        challenge.setBadge(challengeDetails.getBadge());
        return challengeRepository.save(challenge);
    }

    public void deleteChallenge(Long id) {
        challengeRepository.deleteById(id);
    }
}
