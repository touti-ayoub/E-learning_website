package tn.esprit.microservice4.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPoint;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id", nullable = false)
    private Challenge challenge;

    private int pointWins; // Champ qui représente les points attribués

    private String typeActivity;
    private LocalDate dateObtenu;

    // Constructeur par défaut
    public Point() {}

    // Constructeur avec paramètres
    public Point(User user, Challenge challenge, int pointWins, String typeActivity, LocalDate dateObtenu) {
        this.user = user;
        this.challenge = challenge;
        this.pointWins = pointWins;
        this.typeActivity = typeActivity;
        this.dateObtenu = dateObtenu;
    }

    // Getters et Setters
    public Long getIdPoint() {
        return idPoint;
    }

    public void setIdPoint(Long idPoint) {
        this.idPoint = idPoint;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public int getPointWins() {
        return pointWins;
    }

    public void setPointWins(int pointWins) {
        this.pointWins = pointWins; // Assurez-vous d'utiliser la méthode setPointWins
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

    @Override
    public String toString() {
        return "Point{" +
                "idPoint=" + idPoint +
                ", user=" + user +
                ", challenge=" + challenge +
                ", pointWins=" + pointWins +
                ", typeActivity='" + typeActivity + '\'' +
                ", dateObtenu=" + dateObtenu +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return idPoint.equals(point.idPoint);
    }

    @Override
    public int hashCode() {
        return idPoint.hashCode();
    }
}
