package tn.esprit.microservice4.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "badges")
public class Badge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBadge;

    private String name;
    private String description;
    private String iconUrl;

    // Getters and Setters
    public Long getIdBadge() {
        return idBadge;
    }

    public void setIdBadge(Long idBadge) {
        this.idBadge = idBadge;
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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
