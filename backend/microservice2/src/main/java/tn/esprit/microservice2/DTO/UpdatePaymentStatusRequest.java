package tn.esprit.microservice2.DTO;

import lombok.Data;

@Data
public class UpdatePaymentStatusRequest {
    private String status;
}