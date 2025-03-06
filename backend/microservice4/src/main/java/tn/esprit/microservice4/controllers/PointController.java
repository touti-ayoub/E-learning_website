package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Point;
import tn.esprit.microservice4.entities.User;
import tn.esprit.microservice4.entities.Challenge;
import tn.esprit.microservice4.services.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @Autowired
    private PointService pointService;

    // Ajouter des points à un utilisateur
    @PostMapping("/add")
    public ResponseEntity<Point> addPointsToUser(@RequestParam Long userId, @RequestParam Long challengeId) {
        Point point = pointService.addPointsToUser(userId, challengeId);
        return new ResponseEntity<>(point, HttpStatus.CREATED);
    }

    // Récupérer les points d'un utilisateur
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Point>> getUserPoints(@PathVariable Long userId) {
        List<Point> points = pointService.getUserPointsById(userId);
        return new ResponseEntity<>(points, HttpStatus.OK);
    }

    // Supprimer des points
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoints(@PathVariable Long id) {
        pointService.deletePoints(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
