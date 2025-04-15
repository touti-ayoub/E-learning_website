package tn.esprit.microservice4.DTO;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PointDTO {
    private Long userId;
    private String username;
    private Long challengeId;
    private String challengeName;
    private int pointWins;
    private String typeActivity;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateObtenu;

    // Constructeurs
    public PointDTO() {} // Constructeur par défaut nécessaire pour Jackson

    public PointDTO(Long userId, String username, Long challengeId,
                    String challengeName, int pointWins, String typeActivity,
                    LocalDate dateObtenu) {
        this.userId = userId;
        this.username = username;
        this.challengeId = challengeId;
        this.challengeName = challengeName;
        this.pointWins = pointWins;
        this.typeActivity = typeActivity;
        this.dateObtenu = dateObtenu;
    }

    // Getters et Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public int getPointWins() {
        return pointWins;
    }

    public void setPointWins(int pointWins) {
        this.pointWins = pointWins;
    }

    public String getTypeActivity() {
        return typeActivity;
    }

    public void setTypeActivity(String typeActivity) {
        this.typeActivity = typeActivity;
    }

    public LocalDate getDateObtenu() {
        return dateObtenu;
    }

    public void setDateObtenu(LocalDate dateObtenu) {
        this.dateObtenu = dateObtenu;
    }
}