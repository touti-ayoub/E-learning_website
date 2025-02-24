package tn.esprit.microservice1.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.microservice1.entities.User;
import tn.esprit.microservice1.entities.UserRole;
import tn.esprit.microservice1.services.UserService;

@RestController
@RequestMapping({"/users"})
@CrossOrigin({"*"})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(this.userService.getAllUsers());
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return new ResponseEntity(this.userService.createUser(user), HttpStatus.CREATED);
    }

    @PostMapping({"/instructor"})
    public ResponseEntity<User> createInstructor(@RequestBody User user) {
        user.setRole(UserRole.INSTRUCTOR);
        return new ResponseEntity(this.userService.createUser(user), HttpStatus.CREATED);
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(this.userService.updateUser(id, user));
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        this.userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
