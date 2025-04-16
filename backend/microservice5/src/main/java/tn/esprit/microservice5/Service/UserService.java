package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.UserDTO;
import tn.esprit.microservice5.Model.User;
import tn.esprit.microservice5.Repo.IUserRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private IUserRepo userRepository;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(long id) {
        Optional<User> user = userRepository.findById(id);
        return user.map(UserDTO::fromEntity).orElse(null);
    }

    public UserDTO createUser(User user) {
        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);
    }

    public UserDTO updateUser(long id, User userDetails) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User existingUser = userOptional.get();
            existingUser.setUsername(userDetails.getUsername());
            // Only update password if provided
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                existingUser.setPassword(userDetails.getPassword());
            }
            existingUser.setRole(userDetails.getRole());

            User updatedUser = userRepository.save(existingUser);
            return UserDTO.fromEntity(updatedUser);
        }
        return null;
    }

    public boolean deleteUser(long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}