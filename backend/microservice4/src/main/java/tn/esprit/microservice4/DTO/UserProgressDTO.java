package tn.esprit.microservice4.DTO;

import java.time.LocalDate;

public class UserProgressDTO {
    private Long id;
    private Long userId;
    private Long challengeId;
    private int pointsEarned;
    private boolean completed;
    private LocalDate lastUpdated;

    // Constructeurs
    public UserProgressDTO() {}

    public UserProgressDTO(Long id, Long userId, Long challengeId, int pointsEarned, boolean completed, LocalDate lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.challengeId = challengeId;
        this.pointsEarned = pointsEarned;
        this.completed = completed;
        this.lastUpdated = lastUpdated;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void setPointsEarned(int pointsEarned) {
        this.pointsEarned = pointsEarned;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDate getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDate lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
