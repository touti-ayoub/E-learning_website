package tn.esprit.microservice5.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice5.Model.User;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private long userId;
    private String username;
    private String role;

    // Don't include password in DTO for security

    public static UserDTO fromEntity(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        return dto;
    }
}