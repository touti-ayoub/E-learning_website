package tn.esprit.microservice1.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prerequisite_skill")
public class PrerequisiteSkill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String skillName;

    private String description;

    @Column(name = "proficiency_level")
    private Integer proficiencyLevel;

    @Column(name = "is_required")
    private Boolean isRequired;

    @Column(name = "weight_in_course")
    private Double weightInCourse;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;


} 