package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Badge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice4.repositories.BadgeRepository;

import java.util.List;

@RestController
@RequestMapping("/api/backoffice/badges")
public class BadgeController {

    @Autowired
    private BadgeRepository badgeRepository;

    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Badge getBadgeById(@PathVariable Long id) {
        return badgeRepository.findById(id).orElseThrow(() -> new RuntimeException("Badge not found"));
    }

    @PostMapping
    public Badge createBadge(@RequestBody Badge badge) {
        return badgeRepository.save(badge);
    }

    @PutMapping("/{id}")
    public Badge updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        badge.setIdBadge(id);
        return badgeRepository.save(badge);
    }

    @DeleteMapping("/{id}")
    public void deleteBadge(@PathVariable Long id) {
        badgeRepository.deleteById(id);
    }
}
