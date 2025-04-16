package tn.esprit.tpfoyer17.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.entities.User;
import tn.esprit.tpfoyer17.entities.UserDTO;
import tn.esprit.tpfoyer17.repositories.UserRepository;
import tn.esprit.tpfoyer17.services.auth.CustomUserDetailsService;
import tn.esprit.tpfoyer17.services.auth.JwtUtils;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private CustomUserDetailsService userService;
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository,AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> authenticate(@RequestBody User user) {
        // Authenticate the user
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        // Generate the JWT token
        String token = JwtUtils.generateToken(user.getUsername());

        // Retrieve the user from the database to get the ID
        User authenticatedUser = userRepository.findByUsername(user.getUsername());
        if (authenticatedUser == null) {
            return ResponseEntity.badRequest().build(); // Handle case where user is not found
        }

        // Create the UserDTO object
        UserDTO userDTO = new UserDTO();
        userDTO.setId(authenticatedUser.getId()); // Include the user's ID
        userDTO.setUsername(authenticatedUser.getUsername());
        userDTO.setRole(authenticatedUser.getRole());
        userDTO.setToken(token);

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetails> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails user = userService.loadUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/test")
    public String test() {
        return "message from backend successfully";
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (!userOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        User user = userOptional.get();
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        // Add any other fields needed but exclude sensitive data like password

        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/username/{email}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable("email") String email) {
        User user = userRepository.findByUsername(email);
        if (user.getId() == null) {
            return ResponseEntity.notFound().build();
        }
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setUsername(user.getUsername());
        userDTO.setRole(user.getRole());
        // Add any other fields needed but exclude sensitive data like password
        return ResponseEntity.ok(userDTO);
    }
}

