package tn.esprit.microservice4.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_progress")
public class UserProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    @Column(name = "id_course", nullable = false)
    private Long courseId; // Assurez-vous d'avoir une entité Course si nécessaire

    @Column(name = "completed_modules", nullable = false)
    private int completedModules;

    @Column(name = "progress_percentage", nullable = false)
    private double progressPercentage;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
}
