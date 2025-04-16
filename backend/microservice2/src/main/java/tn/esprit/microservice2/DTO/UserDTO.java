package tn.esprit.microservice2.DTO;

import lombok.*;
import tn.esprit.microservice2.Model.User;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String username;
    private String role;

    public UserDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.role = user.getRole();
        }
    }

    public Long getId() {
        return id;
    }
}