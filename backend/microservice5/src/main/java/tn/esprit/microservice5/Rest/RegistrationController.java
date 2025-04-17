package tn.esprit.microservice5.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.DTO.RegistrationDTO;
import tn.esprit.microservice5.Service.RegistrationService;

import java.util.List;

@RestController
@RequestMapping("/mic5/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        List<RegistrationDTO> registrations = registrationService.getAllRegistrations();
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable long id) {
        RegistrationDTO registration = registrationService.getRegistrationById(id);
        return registration != null ? ResponseEntity.ok(registration) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@RequestBody RegistrationDTO registrationDTO) {
        RegistrationDTO createdRegistration = registrationService.createRegistration(registrationDTO);
        return ResponseEntity.ok(createdRegistration);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistrationDTO> updateRegistration(@PathVariable long id, @RequestBody RegistrationDTO registrationDTO) {
        RegistrationDTO updatedRegistration = registrationService.updateRegistration(id, registrationDTO);
        return updatedRegistration != null ? ResponseEntity.ok(updatedRegistration) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable long id) {
        registrationService.deleteRegistration(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByUserId(@PathVariable Long userId) {
        List<RegistrationDTO> registrations = registrationService.getRegistrationsByUserId(userId);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByEventId(@PathVariable Long eventId) {
        List<RegistrationDTO> registrations = registrationService.getRegistrationsByEventId(eventId);
        return ResponseEntity.ok(registrations);
    }
}