package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Badge;
import tn.esprit.microservice4.services.BadgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/backoffice/badges")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    // Récupérer tous les badges
    @GetMapping
    public ResponseEntity<List<Badge>> getAllBadges() {
        List<Badge> badges = badgeService.getAllBadges();
        return new ResponseEntity<>(badges, HttpStatus.OK);
    }

    // Récupérer un badge par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        Badge badge = badgeService.getBadgeById(id);
        return new ResponseEntity<>(badge, HttpStatus.OK);
    }

    // Créer un nouveau badge
    @PostMapping
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) {
        Badge createdBadge = badgeService.createBadge(badge);
        return new ResponseEntity<>(createdBadge, HttpStatus.CREATED);
    }

    // Mettre à jour un badge
    @PutMapping("/{id}")
    public ResponseEntity<Badge> updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        Badge updatedBadge = badgeService.updateBadge(id, badge);
        return new ResponseEntity<>(updatedBadge, HttpStatus.OK);
    }

    // Supprimer un badge
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
