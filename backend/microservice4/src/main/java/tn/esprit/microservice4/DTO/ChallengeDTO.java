package tn.esprit.microservice4.DTO;

import java.time.LocalDate;

public class ChallengeDTO {
    private Long idChallenge;
    private String name;
    private String description;
    private LocalDate createdAt;
    private int rewardPoints;
    private BadgeDTO badge;

    // Constructors
    public ChallengeDTO() {}

    public ChallengeDTO(Long idChallenge, String name, String description,
                        LocalDate createdAt, int rewardPoints, BadgeDTO badge) {
        this.idChallenge = idChallenge;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.rewardPoints = rewardPoints;
        this.badge = badge;
    }

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

    public BadgeDTO getBadge() {
        return badge;
    }

    public void setBadge(BadgeDTO badge) {
        this.badge = badge;
    }
}