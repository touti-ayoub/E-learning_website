package tn.esprit.microservice4.dto;

public class UserDTO {

    private Long id;
    private String username;  // Exemple de champ, tu peux ajouter d'autres propriétés

    // Constructeur par défaut
    public UserDTO() {}

    // Constructeur avec paramètres
    public UserDTO(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    // Getters et Setters
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

    // Méthode toString
    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
