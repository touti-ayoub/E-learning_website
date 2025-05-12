package tn.esprit.microservice4.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice4.dto.CertificateRequest;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.services.CertificateService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/certificates")
@CrossOrigin(origins = "*")
public class CertificateController {

    private static final Logger logger = LoggerFactory.getLogger(CertificateController.class);

    @Autowired
    private CertificateService certificateService;

    @GetMapping("/generate/{examId}")
    public ResponseEntity<?> generateCertificate(@PathVariable Long examId) {
        logger.info("Received request to generate certificate for exam ID: {}", examId);
        try {
            Certificate certificate = certificateService.generateCertificate(examId);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(certificate);
        } catch (Exception e) {
            logger.error("Error generating certificate: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating certificate: " + e.getMessage());
        }
    }

    @GetMapping("/download/{examId}")
    public ResponseEntity<ByteArrayResource> downloadCertificate(@PathVariable Long examId) {
        logger.info("Received download request for exam ID: {}", examId);
        try {
            byte[] data = certificateService.getCertificateByExamId(examId);
            ByteArrayResource resource = new ByteArrayResource(data);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate.pdf");
            headers.add("Access-Control-Expose-Headers", "Content-Disposition");

            logger.info("Successfully retrieved certificate for exam ID: {}", examId);
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(data.length)
                    .body(resource);
        } catch (IOException e) {
            logger.error("Error processing certificate download request: {}", e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error processing certificate download request: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/send/{examId}")
    public ResponseEntity<?> sendCertificateByEmail(
            @PathVariable Long examId,
            @RequestBody CertificateRequest request) {
        logger.info("Received request to send certificate for exam ID {} to email: {}",
                examId, request.getEmail());

        try {
            boolean sent = certificateService.sendCertificateByEmail(examId, request.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("success", sent);
            response.put("message", "Certificate sent successfully to " + request.getEmail());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending certificate by email: {}", e.getMessage(), e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send certificate: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}