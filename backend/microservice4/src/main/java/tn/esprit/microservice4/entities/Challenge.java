package tn.esprit.microservice4.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idChallenge;

    private String name;
    private String description;
    private LocalDate createdAt;
    private int rewardPoints;

    @ManyToOne
    @JoinColumn(name = "id_badge")
    private Badge badge;

    // Getters and Setters
    public Long getIdChallenge() {
        return idChallenge;
    }

    public void setIdChallenge(Long idChallenge) {
        this.idChallenge = idChallenge;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }
}
