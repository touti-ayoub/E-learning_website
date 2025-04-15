package tn.esprit.microservice4.DTO;

public class UserChallengeDTO {
    private Long id;
    private UserDTO user;
    private ChallengeDTO challenge;
    private boolean completed;

    // Constructors
    public UserChallengeDTO() {}

    public UserChallengeDTO(Long id, UserDTO user, ChallengeDTO challenge, boolean completed) {
        this.id = id;
        this.user = user;
        this.challenge = challenge;
        this.completed = completed;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ChallengeDTO getChallenge() {
        return challenge;
    }

    public void setChallenge(ChallengeDTO challenge) {
        this.challenge = challenge;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}