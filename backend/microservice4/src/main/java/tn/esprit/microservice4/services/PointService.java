package tn.esprit.microservice4.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.entities.Point;
import tn.esprit.microservice4.entities.UserProgress;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.repositories.PointRepository;
import tn.esprit.microservice4.repositories.UserProgressRepository;
import tn.esprit.microservice4.repositories.ChallengeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserProgressRepository userProgressRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    // Ajouter des points à un utilisateur pour une activité donnée
    public Point addPoints(Long userProgressId, String activityType, Long challengeId) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new RuntimeException("UserProgress introuvable"));

        Point point = new Point();
        point.setUserProgress(userProgress);
        point.setTypeActivity(activityType);
        point.setDateObtention(LocalDateTime.now());

        if (challengeId != null) {
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new RuntimeException("Challenge introuvable"));
            point.setChallenge(challenge);
            point.setPointWins(challenge.getRewardPoints()); // Utilisez les reward_points du défi
        } else {
            point.setPointWins(0); // Ou une valeur par défaut si ce n'est pas un défi
        }

        return pointRepository.save(point);
    }

    // Récupérer tous les points d'un utilisateur
    public List<Point> getUserPoints(Long userProgressId) {
        return pointRepository.findByUserProgressId(userProgressId);
    }

    // Obtenir le total des points d'un utilisateur
    public int getTotalPoints(Long userProgressId) {
        return pointRepository.getTotalPointsByUserProgress(userProgressId);
    }

    public void addPointsToUser(Long userProgressId, int points, Long challengeId) {
        UserProgress userProgress = userProgressRepository.findById(userProgressId)
                .orElseThrow(() -> new RuntimeException("User Progress not found"));

        Point point = new Point();
        point.setUserProgress(userProgress);
        point.setPointWins(points);
        point.setTypeActivity("Challenge");
        point.setDateObtention(LocalDateTime.now());

        if (challengeId != null) {
            Challenge challenge = challengeRepository.findById(challengeId)
                    .orElseThrow(() -> new RuntimeException("Challenge introuvable"));
            point.setChallenge(challenge);
        }

        pointRepository.save(point);
    }
}