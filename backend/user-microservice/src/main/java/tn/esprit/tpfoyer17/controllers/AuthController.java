package tn.esprit.tpfoyer17.controllers;


import org.springframework.security.crypto.password.PasswordEncoder;
        import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer17.entities.User;
import tn.esprit.tpfoyer17.repositories.UserRepository;
import tn.esprit.tpfoyer17.services.auth.JwtUtils;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String password, @RequestParam String role) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return JwtUtils.generateToken(user.getUsername());
    }
}

