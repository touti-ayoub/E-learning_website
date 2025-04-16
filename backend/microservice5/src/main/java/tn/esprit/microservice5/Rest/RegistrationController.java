package tn.esprit.microservice5.Rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.DTO.RegistrationDTO;
import tn.esprit.microservice5.Model.RegistrationStatus;
import tn.esprit.microservice5.Service.RegistrationService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mic5/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping
    public ResponseEntity<List<RegistrationDTO>> getAllRegistrations() {
        List<RegistrationDTO> registrations = registrationService.getAllRegistrations();
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistrationDTO> getRegistrationById(@PathVariable long id) {
        RegistrationDTO registration = registrationService.getRegistrationById(id);
        if (registration != null) {
            return new ResponseEntity<>(registration, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByUserId(@PathVariable long userId) {
        List<RegistrationDTO> registrations = registrationService.getRegistrationsByUserId(userId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<RegistrationDTO>> getRegistrationsByEventId(@PathVariable long eventId) {
        List<RegistrationDTO> registrations = registrationService.getRegistrationsByEventId(eventId);
        return new ResponseEntity<>(registrations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<RegistrationDTO> createRegistration(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long eventId = request.get("eventId");

        if (userId == null || eventId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        RegistrationDTO createdRegistration = registrationService.createRegistration(userId, eventId);
        if (createdRegistration != null) {
            return new ResponseEntity<>(createdRegistration, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RegistrationDTO> updateRegistrationStatus(
            @PathVariable long id,
            @RequestBody Map<String, String> request) {
        String statusStr = request.get("status");
        if (statusStr == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            RegistrationStatus status = RegistrationStatus.valueOf(statusStr.toUpperCase());
            RegistrationDTO updatedRegistration = registrationService.updateRegistrationStatus(id, status);
            if (updatedRegistration != null) {
                return new ResponseEntity<>(updatedRegistration, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PatchMapping("/{id}/payment")
    public ResponseEntity<RegistrationDTO> updatePaymentStatus(
            @PathVariable long id,
            @RequestBody Map<String, Boolean> request) {
        Boolean paymentStatus = request.get("paymentStatus");
        if (paymentStatus == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        RegistrationDTO updatedRegistration = registrationService.updatePaymentStatus(id, paymentStatus);
        if (updatedRegistration != null) {
            return new ResponseEntity<>(updatedRegistration, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable long id) {
        boolean deleted = registrationService.deleteRegistration(id);
        if (deleted) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}