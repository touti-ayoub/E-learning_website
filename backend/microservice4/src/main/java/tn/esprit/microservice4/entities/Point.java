package tn.esprit.microservice4.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPoint;

    private Long userId;  // Stocke l'ID de l'utilisateur du DTO

    private int pointWins;
    private String typeActivity;
    private LocalDate dateObtenu;

    // Constructeur par défaut
    public Point() {}

    // Constructeur avec paramètres
    public Point(Long userId, int pointWins, String typeActivity, LocalDate dateObtenu) {
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    // Méthode toString
    @Override
    public String toString() {
        return "Point{" +
                "idPoint=" + idPoint +
                ", userId=" + userId +
                ", pointWins=" + pointWins +
                ", typeActivity='" + typeActivity + '\'' +
                ", dateObtenu=" + dateObtenu +
                '}';
    }

    // Méthode equals & hashCode
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
