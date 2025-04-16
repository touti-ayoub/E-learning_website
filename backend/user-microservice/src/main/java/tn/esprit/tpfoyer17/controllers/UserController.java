package tn.esprit.tpfoyer17.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.entities.User;
import tn.esprit.tpfoyer17.entities.UserDTO;
import tn.esprit.tpfoyer17.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.entities.User;
import tn.esprit.tpfoyer17.entities.UserDTO;
import tn.esprit.tpfoyer17.repositories.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setRole(user.getRole());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/validate/{id}")
    public ResponseEntity<Boolean> validateUser(@PathVariable Long id) {
        boolean exists = userRepository.existsById(id);
        return ResponseEntity.ok(exists);
    }
}