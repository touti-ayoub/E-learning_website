package tn.esprit.microservice2.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "paiement_id")
    private Payment payment;

    private String invoiceNumber;       // Numéro de facture
    private String pdfUrl;              // URL du PDF de la facture
    private LocalDate issueDate;        // Date d'émission
    private LocalDate dueDate;          // Date d'échéance
    private Double totalAmount;         // Montant total
    private Double taxAmount;           // Montant des taxes
    private LocalDateTime createdAt;    // Date de création
}
