package tn.esprit.microservice2.service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        javaMailSender.send(message);
    }

    /**
     * Send email with attachment
     */
    public void sendEmailWithAttachment(String to, String subject, String body,
                                        byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        // Add attachment
        ByteArrayResource resource = new ByteArrayResource(attachment);
        helper.addAttachment(attachmentName, resource);

        javaMailSender.send(message);
    }

    /**
     * Send invoice email
     */
    public void sendInvoiceEmail(String to, String userName, String invoiceNumber,
                                 byte[] pdfInvoice) throws MessagingException {
        String subject = "Your Invoice #" + invoiceNumber;
        String body = String.format(
                "<html><body>" +
                        "<h1>Invoice #%s</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for your payment. Please find attached your invoice.</p>" +
                        "<p>If you have any questions about this invoice, please contact our support team.</p>" +
                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                        "</body></html>",
                invoiceNumber, userName);

        sendEmailWithAttachment(to, subject, body, pdfInvoice, "Invoice_" + invoiceNumber + ".pdf");
    }

    /**
     * Send payment confirmation email
     */
    public void sendPaymentConfirmationEmail(String to, String userName,
                                             String courseTitle, String amount) throws MessagingException {
        String subject = "Payment Confirmation";
        String body = String.format(
                "<html><body>" +
                        "<h1>Payment Confirmation</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>Thank you for your payment of %s for the course \"%s\".</p>" +
                        "<p>Your payment has been successfully processed.</p>" +
                        "<p>You can now access your course content in your learning dashboard.</p>" +
                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                        "</body></html>",
                userName, amount, courseTitle);

        sendEmail(to, subject, body);
    }

    /**
     * Send email when all installments are completed
     */
    public void sendAllInstallmentsCompletedEmail(String to, String userName,
                                                  String courseTitle, String totalAmount) throws MessagingException {
        String subject = "All Installments Completed";
        String body = String.format(
                "<html><body>" +
                        "<h1>Payment Plan Completed</h1>" +
                        "<p>Dear %s,</p>" +
                        "<p>Congratulations! You have successfully completed all installment payments " +
                        "totaling %s for the course \"%s\".</p>" +
                        "<p>Thank you for your payments.</p>" +
                        "<p>Best regards,<br/>The eLEARNING Team</p>" +
                        "</body></html>",
                userName, totalAmount, courseTitle);

        sendEmail(to, subject, body);
    }
}