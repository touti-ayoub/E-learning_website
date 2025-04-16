package tn.esprit.microservice1.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This is a reference entity that represents a User from the user-microservice.
 * We only store the ID as a reference to the actual user in the user-microservice.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    private Long id;
    
    // No additional fields needed as this is just a reference entity
} 

