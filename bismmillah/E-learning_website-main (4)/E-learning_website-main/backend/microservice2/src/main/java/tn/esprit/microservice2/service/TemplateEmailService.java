package tn.esprit.microservice2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class TemplateEmailService {

    @Autowired
    private EmailService emailService;

    /**
     * Send an email using a template
     */
    public void sendTemplatedEmail(String to, String subject, String templateName,
                                   Map<String, String> variables) throws Exception {
        String template = loadTemplate(templateName);
        String content = replaceTemplateVariables(template, variables);
        emailService.sendEmail(to, subject, content);
    }

    /**
     * Send payment reminder using the HTML template
     */
    public void sendPaymentReminderEmail(String to, String userName, String courseName,
                                         Double amount, String currency, LocalDate dueDate,
                                         Integer installmentNumber, Integer totalInstallments,
                                         Integer daysRemaining, String paymentLink) throws Exception {

        Map<String, String> variables = new HashMap<>();
        variables.put("userName", userName);
        variables.put("courseName", courseName);
        variables.put("amount", String.format("%.2f", amount));
        variables.put("currency", currency);
        variables.put("dueDate", dueDate.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        variables.put("installmentNumber", installmentNumber.toString());
        variables.put("totalInstallments", totalInstallments.toString());
        variables.put("daysRemaining", daysRemaining.toString());
        variables.put("dayText", daysRemaining == 1 ? "day" : "days");
        variables.put("paymentLink", paymentLink);
        variables.put("currentYear", String.valueOf(LocalDate.now().getYear()));

        // Add urgent prefix if due very soon
        variables.put("urgentPrefix", daysRemaining <= 1 ?
                "<span class=\"urgent\">URGENT: </span>" : "");

        String subject = (daysRemaining <= 1 ? "URGENT: " : "") +
                "Payment Reminder: Installment Due in " + daysRemaining +
                (daysRemaining == 1 ? " day" : " days");

        sendTemplatedEmail(to, subject, "payment-reminder-template.html", variables);
    }

    /**
     * Load a template from the classpath
     */
    private String loadTemplate(String templateName) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templateName);
        try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
            return FileCopyUtils.copyToString(reader);
        }
    }

    /**
     * Replace variables in the template
     */
    private String replaceTemplateVariables(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("${" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}