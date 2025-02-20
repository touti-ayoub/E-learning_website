package tn.esprit.microservice4.controllers;

import tn.esprit.microservice4.entities.Point;
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

    // Récupérer tous les points
    @GetMapping
    public ResponseEntity<List<Point>> getAllPoints() {
        List<Point> points = pointService.getAllPoints();
        return new ResponseEntity<>(points, HttpStatus.OK);
    }

    // Récupérer un point par ID
    @GetMapping("/{id}")
    public ResponseEntity<Point> getPointById(@PathVariable Long id) {
        Point point = pointService.getPointById(id);
        return new ResponseEntity<>(point, HttpStatus.OK);
    }

    // Créer un nouveau point
    @PostMapping
    public ResponseEntity<Point> createPoint(@RequestBody Point point) {
        Point createdPoint = pointService.createPoint(point);
        return new ResponseEntity<>(createdPoint, HttpStatus.CREATED);
    }

    // Mettre à jour un point existant
    @PutMapping("/{id}")
    public ResponseEntity<Point> updatePoint(@PathVariable Long id, @RequestBody Point point) {
        Point updatedPoint = pointService.updatePoint(id, point);
        return new ResponseEntity<>(updatedPoint, HttpStatus.OK);
    }

    // Supprimer un point par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePoint(@PathVariable Long id) {
        pointService.deletePoint(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
