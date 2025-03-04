package tn.esprit.microservice4.services;

import tn.esprit.microservice4.entities.Point;
import tn.esprit.microservice4.entities.User;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.repositories.PointRepository;
import tn.esprit.microservice4.repositories.UserRepository;
import tn.esprit.microservice4.repositories.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    // Ajouter des points à un utilisateur
    @Transactional
    public Point addPointsToUser(Long userId, Long challengeId) {
        // Vérifier si l'utilisateur et le défi existent
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new IllegalArgumentException("Challenge with ID " + challengeId + " does not exist"));

        // Créer un nouveau point pour l'utilisateur
        Point point = new Point();
        point.setUser(user);  // Lier l'utilisateur à ce point
        point.setChallenge(challenge);
        point.setPointWins(challenge.getRewardPoints());  // Utiliser les points de récompense du défi

        // Sauvegarder l'attribution des points dans la base de données
        return pointRepository.save(point);
    }

    // Récupérer les points d'un utilisateur par son ID
    public List<Point> getUserPointsById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with ID " + userId + " does not exist"));
        return pointRepository.findByUser(user);
    }

    // Supprimer des points par ID
    @Transactional
    public void deletePoints(Long id) {
        if (!pointRepository.existsById(id)) {
            throw new IllegalArgumentException("Point with ID " + id + " does not exist");
        }
        pointRepository.deleteById(id);
    }
}
