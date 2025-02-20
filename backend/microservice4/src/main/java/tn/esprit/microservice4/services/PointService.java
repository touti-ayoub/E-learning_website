package tn.esprit.microservice4.services;

import tn.esprit.microservice4.entities.Point;
import tn.esprit.microservice4.repositories.PointRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PointService {

    @Autowired
    private PointRepository pointRepository;

    public List<Point> getAllPoints() {
        return pointRepository.findAll();
    }

    public Point getPointById(Long id) {
        return pointRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Point not found with ID: " + id));
    }

    @Transactional
    public Point createPoint(Point point) {
        if (point.getPointWins() <= 0) {
            throw new IllegalArgumentException("Point wins must be greater than zero");
        }
        return pointRepository.save(point);
    }

    @Transactional
    public Point updatePoint(Long id, Point point) {
        if (!pointRepository.existsById(id)) {
            throw new EntityNotFoundException("Point not found with ID: " + id);
        }
        point.setIdPoint(id);
        return pointRepository.save(point);
    }

    @Transactional
    public void deletePoint(Long id) {
        if (!pointRepository.existsById(id)) {
            throw new EntityNotFoundException("Point not found with ID: " + id);
        }
        pointRepository.deleteById(id);
    }
}
