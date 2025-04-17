package tn.esprit.microservice2.comm;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import tn.esprit.microservice2.DTO.UserDTO;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public UserDTO getUserById(Long id) {
                UserDTO fallbackUser = new UserDTO();
                fallbackUser.setId(null);
                fallbackUser.setUsername("unavailable");
                //fallbackUser.setEmail("unavailable");
                fallbackUser.setRole("unavailable");
                // Log the error
                System.err.println("Error fetching user by ID: " + cause.getMessage());
                return fallbackUser;
            }

            @Override
            public UserDTO getUserByUsername(String email) {
                UserDTO fallbackUser = new UserDTO();
                fallbackUser.setId(null);
                fallbackUser.setUsername("unavailable");
                fallbackUser.setRole("unavailable");
                // Log the error
                System.err.println("Error fetching user by email: " + cause.getMessage());
                return fallbackUser;
            }
        };
    }
}