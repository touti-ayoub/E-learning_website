package tn.esprit.microservice4.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import tn.esprit.microservice4.entities.Point;
import tn.esprit.microservice4.services.PointService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mic4/points")
public class PointController {

    @Autowired
    private PointService pointService;

    // Ajouter des points à un utilisateur
    @PostMapping("/add")
    public Point addPoints(@RequestParam Long userProgressId,
                           @RequestParam String activityType,
                           @RequestParam(required = false) Long challengeId) {
        return pointService.addPoints(userProgressId, activityType, challengeId);
    }
    // Obtenir l'historique des points d'un utilisateur
    @GetMapping("/user/{userProgressId}")
    public List<Point> getUserPoints(@PathVariable Long userProgressId) {
        return pointService.getUserPoints(userProgressId);
    }

    // Obtenir le total des points d'un utilisateur
    @GetMapping("/user/{userProgressId}/total")
    public Map<String, Integer> getTotalPoints(@PathVariable Long userProgressId) {
        int total = pointService.getTotalPoints(userProgressId);
        Map<String, Integer> response = new HashMap<>();
        response.put("totalPoints", total);
        return response;
    }


}