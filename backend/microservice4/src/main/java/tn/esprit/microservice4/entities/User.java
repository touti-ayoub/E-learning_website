package tn.esprit.microservice4.entities;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role; // Example: ROLE_USER, ROLE_ADMIN

    // Many-to-Many relationship with Badge
    @ManyToMany
    @JoinTable(
            name = "user_badge",  // Join table name
            joinColumns = @JoinColumn(name = "user_id"),  // Foreign key for User
            inverseJoinColumns = @JoinColumn(name = "badge_id")  // Foreign key for Badge
    )
    private Set<Badge> badges = new HashSet<>();

    // Default constructor
    public User() {
    }

    // Constructor with all attributes
    public User(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Badge> getBadges() {
        return badges;
    }

    public void setBadges(Set<Badge> badges) {
        this.badges = badges;
    }

    // Add a badge to the user
    public void addBadge(Badge badge) {
        this.badges.add(badge);
    }

    // toString method (optional, but useful for debugging)
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", badges=" + badges +
                '}';
    }
}
