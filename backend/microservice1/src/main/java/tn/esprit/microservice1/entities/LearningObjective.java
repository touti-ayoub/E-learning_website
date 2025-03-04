package tn.esprit.microservice1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "learning_objective")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LearningObjective {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String objective;

    private String description;

    @Column(name = "difficulty_level")
    private Integer difficultyLevel;

    @Column(name = "completion_threshold")
    private Double completionThreshold;

    @Column(name = "is_ai_generated")
    private Boolean isAiGenerated;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
} 