package tn.esprit.microservice4.DTO;

public class BadgeDTO {
    private Long idBadge;
    private String name;
    private String description;
    private String iconUrl;

    // Constructors
    public BadgeDTO() {}

    public BadgeDTO(Long idBadge, String name, String description, String iconUrl) {
        this.idBadge = idBadge;
        this.name = name;
        this.description = description;
        this.iconUrl = iconUrl;
    }

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