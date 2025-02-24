package tn.esprit.microservice1.services;

import java.util.List;
import tn.esprit.microservice1.entities.User;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    User createUser(User user);

    User updateUser(Long id, User user);

    void deleteUser(Long id);
}
