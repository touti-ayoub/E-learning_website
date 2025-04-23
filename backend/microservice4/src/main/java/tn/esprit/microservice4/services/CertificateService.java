package tn.esprit.microservice4.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.client.UserClient;
import tn.esprit.microservice4.dto.UserDTO;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.entities.Exam;
import tn.esprit.microservice4.repositories.CertificateRepository;
import tn.esprit.microservice4.repositories.ExamRepository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class CertificateService {

    private static final Logger logger = LoggerFactory.getLogger(CertificateService.class);

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private MailService mailService;
    @Autowired
    private UserClient userClient;

    @Value("${file.certificate-dir:certificates}")
    private String certificatesDir;

    private Path getCertificatesPath() {
        // Get the absolute path to the certificates directory
        Path basePath = Paths.get(System.getProperty("user.dir"));
        Path certificatesPath = basePath.resolve(certificatesDir);
        logger.info("Using certificates directory: {}", certificatesPath);
        return certificatesPath;
    }

    public Certificate generateCertificate(Long examId) {
        try {
            logger.info("Generating certificate for exam ID: {}", examId);

            // First check if certificate already exists
            Optional<Certificate> existingCertificate = certificateRepository.findByExam_Id(examId);
            if (existingCertificate.isPresent()) {
                logger.info("Certificate already exists for exam ID: {}", examId);
                return existingCertificate.get();
            }

            // Get exam details
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("Exam not found"));

            // Validate exam status
            if (exam.getStatus() != Exam.ExamStatus.PASSED) {
                throw new RuntimeException("Cannot generate certificate for non-passed exam");
            }

            // Validate required data
            if (exam.getTitle() == null || exam.getUserId() == null) {
                throw new RuntimeException("Missing required exam data for certificate generation");
            }

            // Create certificates directory if it doesn't exist
            Path certificatesPath = getCertificatesPath();
            if (!Files.exists(certificatesPath)) {
                logger.info("Creating certificates directory: {}", certificatesPath);
                Files.createDirectories(certificatesPath);
            }

            // Generate unique filename
            String fileName = String.format("certificate_%d_%s.pdf",
                    examId,
                    System.currentTimeMillis());
            Path filePath = certificatesPath.resolve(fileName);
            logger.info("Generated certificate file path: {}", filePath);

            // Create PDF document
// Create PDF document with margins
            Document document = new Document(PageSize.A4.rotate(), 50, 50, 50, 50);
            try {
                Files.createDirectories(filePath.getParent());

                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));
                document.open();

                // Optional: Add logo at the top-left (put logo.png in resources)
                try {
                    Image logo = Image.getInstance("src/main/resources/logo.png");
                    logo.scaleToFit(100, 100);
                    logo.setAbsolutePosition(document.left(), document.top() - 100);
                    document.add(logo);
                } catch (Exception ex) {
                    logger.warn("Logo not found or error loading logo: {}", ex.getMessage());
                }

                // Add border
                PdfContentByte canvas = writer.getDirectContent();
                Rectangle rect = new Rectangle(document.getPageSize());
                rect.setLeft(20);
                rect.setRight(document.getPageSize().getWidth() - 20);
                rect.setTop(document.getPageSize().getHeight() - 20);
                rect.setBottom(20);
                rect.setBorder(Rectangle.BOX);
                rect.setBorderWidth(3);
                rect.setBorderColor(BaseColor.DARK_GRAY);
                canvas.rectangle(rect);

                // Title
                Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 40, Font.BOLD, BaseColor.DARK_GRAY);
                Paragraph title = new Paragraph("Certificate of Achievement", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(40f);
                document.add(title);

                // Subtitle
                Font subtitleFont = new Font(Font.FontFamily.TIMES_ROMAN, 20, Font.ITALIC, BaseColor.GRAY);
                Paragraph subtitle = new Paragraph("This certifies that", subtitleFont);
                subtitle.setAlignment(Element.ALIGN_CENTER);
                subtitle.setSpacingAfter(20f);
                document.add(subtitle);

                // Student
                UserDTO user = userClient.getUserById(exam.getUserId());
                String fullName = user.getUsername();

                Font nameFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.BLACK);
             //   Paragraph studentInfo = new Paragraph(fullName + " (ID: " + exam.getUserId() + ")", nameFont);
                Paragraph studentInfo = new Paragraph(fullName , nameFont);

                studentInfo.setAlignment(Element.ALIGN_CENTER);
                studentInfo.setSpacingAfter(20f);
                document.add(studentInfo);


                // Exam Info
                Font bodyFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.NORMAL, BaseColor.BLACK);
                Paragraph body = new Paragraph(
                        "has successfully passed the exam entitled\n\n" +
                                "\"" + exam.getTitle() + "\"\n\n" +
                                "with a final score of " + exam.getScore() + "%", bodyFont);
                body.setAlignment(Element.ALIGN_CENTER);
                body.setSpacingAfter(40f);
                document.add(body);

                // Date
                Font dateFont = new Font(Font.FontFamily.COURIER, 14, Font.ITALIC, BaseColor.DARK_GRAY);
                Paragraph date = new Paragraph("Date Issued: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), dateFont);
                date.setAlignment(Element.ALIGN_RIGHT);
                document.add(date);

                document.close();
                logger.info("Successfully generated enhanced PDF certificate at: {}", filePath);


                // Verify the file was created
                if (!Files.exists(filePath)) {
                    throw new RuntimeException("Certificate file was not created at: " + filePath);
                }

                // Create and save certificate entity
                Certificate certificate = new Certificate();
                certificate.setCertificateUrl(filePath.toString());
                certificate.setIssuedDate(LocalDateTime.now());
                certificate.setExam(exam);

                Certificate savedCertificate = certificateRepository.save(certificate);

                // Update exam with certificate info
                exam.setCertificate(savedCertificate);
                exam.setCertificateGenerated(true);
                exam.setCertificateUrl(filePath.toString());
                examRepository.save(exam);

                logger.info("Successfully saved certificate entity with URL: {}", filePath);
                return savedCertificate;
            } catch (Exception e) {
                // Clean up the file if it was created
                Files.deleteIfExists(filePath);
                logger.error("Error generating certificate: {}", e.getMessage(), e);
                throw new Exception("Error generating certificate: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Error in generateCertificate: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating certificate: " + e.getMessage());
        }
    }

    public byte[] getCertificateFile(String certificateUrl) throws IOException {
        logger.info("Getting certificate file from URL: {}", certificateUrl);
        Path path = Paths.get(certificateUrl);

        if (!Files.exists(path)) {
            logger.error("Certificate file not found at path: {}", path);
            // Try to find the file in the certificates directory
            Path certificatesPath = getCertificatesPath();
            Path alternativePath = certificatesPath.resolve(path.getFileName());
            if (Files.exists(alternativePath)) {
                logger.info("Found certificate file at alternative path: {}", alternativePath);
                return Files.readAllBytes(alternativePath);
            }
            throw new IOException("Certificate file not found at any location: " + certificateUrl);
        }

        return Files.readAllBytes(path);
    }

    public byte[] getCertificateByExamId(Long examId) throws IOException {
        logger.info("Getting certificate for exam ID: {}", examId);

        // Get exam details
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Check if the exam has a certificate
        if (exam.getCertificate() == null) {
            throw new RuntimeException("No certificate found for this exam");
        }

        // Get certificate URL
        String certificateUrl = exam.getCertificate().getCertificateUrl();
        if (certificateUrl == null || certificateUrl.isEmpty()) {
            throw new RuntimeException("Certificate URL is not set");
        }

        // Get certificate file
        return getCertificateFile(certificateUrl);
    }

    public boolean sendCertificateByEmail(Long examId, String email) {
        try {
            logger.info("Sending certificate for exam ID {} to email: {}", examId, email);

            // Make sure certificate exists or generate it
            Certificate certificate;
            try {
                certificate = generateCertificate(examId);
            } catch (Exception e) {
                // Certificate might already exist
                certificate = certificateRepository.findByExam_Id(examId)
                        .orElseThrow(() -> new RuntimeException("Could not generate or find certificate"));
            }

            // Get exam details
            Exam exam = examRepository.findById(examId)
                    .orElseThrow(() -> new RuntimeException("Exam not found"));

            // Get certificate file
            byte[] certificateBytes = getCertificateFile(certificate.getCertificateUrl());

            // Send email with certificate
            String subject = "Your Certificate for " + exam.getTitle();
            String message = "Congratulations on passing the exam '" + exam.getTitle() +
                    "' with a score of " + exam.getScore() + "%!\n\n" +
                    "Please find your certificate attached to this email.\n\n" +
                    "Best regards,\nThe Exam Team";

            mailService.sendEmailWithAttachment(
                    email,
                    subject,
                    message,
                    certificateBytes,
                    "certificate_" + examId + ".pdf"
            );

            logger.info("Successfully sent certificate for exam ID {} to email: {}", examId, email);
            return true;
        } catch (Exception e) {
            logger.error("Error sending certificate by email: {}", e.getMessage(), e);
            throw new RuntimeException("Error sending certificate by email: " + e.getMessage());
        }
    }
}