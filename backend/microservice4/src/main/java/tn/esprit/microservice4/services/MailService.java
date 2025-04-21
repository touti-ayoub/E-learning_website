package tn.esprit.microservice4.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import tn.esprit.microservice4.dto.UserDTO;
import tn.esprit.microservice4.entities.Certificate;
import tn.esprit.microservice4.client.UserClient;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final UserClient userClient;

    public MailService(JavaMailSender mailSender, UserClient userClient) {
        this.mailSender = mailSender;
        this.userClient = userClient;
    }

    public void sendCertificateNotification(Long userId, Certificate certificate) {
        UserDTO user = userClient.getUserById(userId);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(user.getEmail());
        msg.setSubject("Your Certificate is Ready");
        msg.setText("Hello " + user.getUsername() + ",\n\nYou passed the exam. "
                + "Here is your certificate: " + certificate.getCertificateUrl());
        mailSender.send(msg);
    }
}
