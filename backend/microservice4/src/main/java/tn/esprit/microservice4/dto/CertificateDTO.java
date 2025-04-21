package tn.esprit.microservice4.dto;

import java.time.LocalDateTime;

public class CertificateDTO {
    private String certificateUrl;
    private LocalDateTime issuedDate;

    public String getCertificateUrl() { return certificateUrl; }
    public void setCertificateUrl(String certificateUrl) { this.certificateUrl = certificateUrl; }

    public LocalDateTime getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDateTime issuedDate) { this.issuedDate = issuedDate; }
}
