package tn.esprit.microservice1.services.impl;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.repositories.UserRepository;
import tn.esprit.microservice1.services.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(
            readOnly = true
    )
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Transactional(
            readOnly = true
    )
    public User getUserById(Long id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        return (User)this.userRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("User not found with id: " + String.valueOf(id));
        });
    }

    public User createUser(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        if (user.getLastLogin() == null) {
            user.setLastLogin(LocalDateTime.now());
        }

        return (User)this.userRepository.save(user);
    }

    public User updateUser(Long id, User user) {
        Objects.requireNonNull(id, "User ID cannot be null");
        Objects.requireNonNull(user, "User cannot be null");
        if (!this.userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + String.valueOf(id));
        } else {
            user.setId(id);
            return (User)this.userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        Objects.requireNonNull(id, "User ID cannot be null");
        if (!this.userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + String.valueOf(id));
        } else {
            this.userRepository.deleteById(id);
        }
    }
}
