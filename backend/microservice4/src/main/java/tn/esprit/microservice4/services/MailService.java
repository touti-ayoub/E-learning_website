package tn.esprit.microservice4.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.entities.Certificate;

@Service
public class MailService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendCertificateNotification(Long userId, Certificate certificate) {
        // This method would be implemented to send an email to user
        // notifying them that a certificate is available
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Your Certificate is Ready");
        message.setText("Your certificate for exam is ready!");
        // In a real application, you would get user's email from a user repository
        // message.setTo(email);
        logger.info("Would send certificate notification to user {}", userId);
    }

    public void sendEmailWithAttachment(String to, String subject, String text,
                                        byte[] attachment, String attachmentFilename)
            throws MessagingException {
        logger.info("Sending email with attachment to: {}", to);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        // Add the attachment
        helper.addAttachment(attachmentFilename, new ByteArrayResource(attachment));

        mailSender.send(message);
        logger.info("Email with attachment sent successfully to: {}", to);
    }
}