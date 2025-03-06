package tn.esprit.microservice2.service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import org.hibernate.sql.exec.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.microservice2.Model.*;
import tn.esprit.microservice2.repo.IInvoiceRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class InvoiceService {


    @Value("${app.company.logo:}")  // Empty default value if not specified
    private String companyLogo;

    @Value("${app.company.name}")
    private String companyName;

    @Value("${app.company.address}")
    private String companyAddress;

    @Value("${app.company.email}")
    private String companyEmail;

    @Value("${app.company.phone}")
    private String companyPhone;

    @Value("${app.company.website}")
    private String companyWebsite;

    @Value("${app.invoice.terms}")
    private String invoiceTerms;

    @Autowired
    private IInvoiceRepository invoiceRepository;

    public InvoiceService() {
        // Default constructor
    }
    // Generate unique invoice number with formatting
    private String generateInvoiceNumber(Payment payment) {
        LocalDateTime now = LocalDateTime.now();
        String yearMonth = DateTimeFormatter.ofPattern("yyMM").format(now);

        // Get count of invoices for this month and increment
        long count = payment.getId();//invoiceRepository.countByYearMonth(yearMonth) + 1;

        // Format: INV-YYMM-SEQUENCE (e.g., INV-2503-00123)
        return String.format("INV-%s-%05d", yearMonth, count);
    }
    // Create invoice record in database
// Create invoice record in database
    public Invoice createInvoice(Payment payment, User user) {
        LocalDateTime now = LocalDateTime.now();

        Invoice invoice = new Invoice();
        invoice.setPayment(payment);
        invoice.setInvoiceNumber(generateInvoiceNumber(payment));
        invoice.setTotalAmount(payment.getAmount());
        invoice.setSubtotal(calculateSubtotal(payment.getAmount()));
        invoice.setTaxAmount(calculateTax(payment.getAmount()));
        invoice.setIssuedDate(now);
        invoice.setDueDate(now);
        invoice.setStatus(payment.getStatus() == PaymentStatus.SUCCESS ?
                InvoiceStatus.PAID : InvoiceStatus.UNPAID);
        invoice.setUserId(user.getId());
        invoice.setUserName(user.getUsername());
        invoice.setCurrency(payment.getCurrency());
        invoice.setPaymentMethod(payment.getPaymentMethod());

        // Add course information if available
        if (payment.getSubscription() != null && payment.getSubscription().getCourse() != null) {
            invoice.setCourseId(payment.getSubscription().getCourse().getId());
            invoice.setCourseName(payment.getSubscription().getCourse().getTitle());
        }

        return invoiceRepository.save(invoice);
    }
    // Calculate subtotal (before tax)
    private BigDecimal calculateSubtotal(BigDecimal total) {
        // If your tax is included in the price, extract it here
        // Otherwise just return the total
        return total.divide(BigDecimal.valueOf(1.2), 2, RoundingMode.HALF_UP); // Example for 20% tax
    }

    // Calculate tax amount
    private BigDecimal calculateTax(BigDecimal total) {
        // Calculate based on your tax rules
        BigDecimal subtotal = calculateSubtotal(total);
        return total.subtract(subtotal).setScale(2, RoundingMode.HALF_UP);
    }

    // Generate PDF in memory and return as byte array
    public byte[] generateInvoicePdf(Long invoiceId) throws DocumentException, IOException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ExecutionException("Invoice not found"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, baos);
        document.open();

        // Add metadata
        document.addTitle("Invoice #" + invoice.getInvoiceNumber());
        document.addAuthor(companyName);
        document.addCreator(companyName);

        // Add header with company logo
        addHeader(document);

        // Add invoice details
        addInvoiceDetails(document, invoice);

        // Add customer information
        addCustomerInfo(document, invoice);

        // Add itemized charges
        addItemizedCharges(document, invoice);

        // Add payment summary
        addPaymentSummary(document, invoice);

        // Add footer with terms and contact info
        addFooter(document);

        document.close();

        return baos.toByteArray();
    }



    private void addHeader(Document document) throws DocumentException, IOException {
        // Try to add company logo if available
        if (companyLogo != null && !companyLogo.isEmpty()) {
            try {
                Image logo = Image.getInstance(companyLogo);
                logo.scaleToFit(150, 75);
                logo.setAlignment(Element.ALIGN_LEFT);
                document.add(logo);
            } catch (Exception e) {
                System.err.println("Could not load company logo: " + e.getMessage());
            }
        }

        // Add company info
        Paragraph companyInfo = new Paragraph();
        companyInfo.add(new Chunk(companyName + "\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
        companyInfo.add(new Chunk(companyAddress + "\n", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        companyInfo.add(new Chunk("Phone: " + companyPhone + "\n", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        companyInfo.add(new Chunk("Email: " + companyEmail + "\n", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        companyInfo.add(new Chunk("Website: " + companyWebsite, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        companyInfo.setAlignment(Element.ALIGN_RIGHT);
        document.add(companyInfo);

        // Add separator line
        LineSeparator line = new LineSeparator();
        line.setLineWidth(0.5f);
        line.setPercentage(100);
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));

        document.add(Chunk.NEWLINE);
    }

    private void addInvoiceDetails(Document document, Invoice invoice) throws DocumentException {
        // Create a table for invoice details
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);

        // Add invoice title
        PdfPCell cell = new PdfPCell(new Phrase("INVOICE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.DARK_GRAY)));
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPaddingBottom(20);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Add invoice number
        cell = new PdfPCell(new Phrase("Invoice Number:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(invoice.getInvoiceNumber(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        // Add issue date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

        cell = new PdfPCell(new Phrase("Issue Date:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(formatter.format(invoice.getIssuedDate()), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        // Add due date
        cell = new PdfPCell(new Phrase("Due Date:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(formatter.format(invoice.getDueDate()), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        // Add status
        cell = new PdfPCell(new Phrase("Status:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);

        BaseColor statusColor = invoice.getStatus() == InvoiceStatus.PAID ? new BaseColor(0, 150, 0) : new BaseColor(200, 0, 0);
        cell = new PdfPCell(new Phrase(invoice.getStatus().toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, statusColor)));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);

        document.add(table);
        document.add(Chunk.NEWLINE);
    }

    private void addCustomerInfo(Document document, Invoice invoice) throws DocumentException {
        // Add customer title
        Paragraph customerTitle = new Paragraph("BILLED TO:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12));
        customerTitle.setSpacingBefore(10);
        customerTitle.setSpacingAfter(5);
        document.add(customerTitle);

        // Add customer info
        Paragraph customerInfo = new Paragraph();
        customerInfo.add(new Chunk(invoice.getUserName() + "\n", FontFactory.getFont(FontFactory.HELVETICA, 11)));

        document.add(customerInfo);

        document.add(Chunk.NEWLINE);
    }

    private void addItemizedCharges(Document document, Invoice invoice) throws DocumentException {
        // Create table for items
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {5, 1, 2, 2});
        table.setSpacingBefore(15);

        // Table headers
        PdfPCell cell = new PdfPCell(new Phrase("Description", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(66, 103, 178)); // Blue header
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Qty", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(66, 103, 178));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Rate", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(66, 103, 178));
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("Amount", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBackgroundColor(new BaseColor(66, 103, 178));
        table.addCell(cell);

        // Item details
        String description = invoice.getCourseName();


        cell = new PdfPCell(new Phrase(description, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase("1", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8);
        table.addCell(cell);

        String currency = invoice.getCurrency();
        String currencySymbol = currency.equals("EUR") ? "€" : "$";

        cell = new PdfPCell(new Phrase(currencySymbol + invoice.getSubtotal().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(8);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(currencySymbol + invoice.getSubtotal().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(8);
        table.addCell(cell);

        document.add(table);
    }

    private void addPaymentSummary(Document document, Invoice invoice) throws DocumentException {
        // Summary table
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(40);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(15);

        String currency = invoice.getCurrency();
        String currencySymbol = currency.equals("EUR") ? "€" : "$";

        // Subtotal
        PdfPCell cell = new PdfPCell(new Phrase("Subtotal:", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(currencySymbol + invoice.getSubtotal().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Tax
        cell = new PdfPCell(new Phrase("Tax:", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(currencySymbol + invoice.getTaxAmount().toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Line above total
        cell = new PdfPCell();
        cell.setColspan(2);
        cell.setBorder(Rectangle.TOP);
        cell.setBorderWidth(1);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
        cell.setFixedHeight(2);
        table.addCell(cell);

        // Total
        cell = new PdfPCell(new Phrase("Total:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(currencySymbol + invoice.getTotalAmount().toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPadding(5);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        // Payment method
        cell = new PdfPCell(new Phrase("Payment Method:", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPaddingTop(10);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(invoice.getPaymentMethod(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setPaddingTop(10);
        cell.setBorder(Rectangle.NO_BORDER);
        table.addCell(cell);

        document.add(table);
    }

    private void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        // Add separator line
        LineSeparator line = new LineSeparator();
        line.setLineWidth(0.5f);
        line.setPercentage(100);
        line.setLineColor(BaseColor.LIGHT_GRAY);
        document.add(new Chunk(line));

        // Add terms and notes
        Paragraph terms = new Paragraph();
        terms.setSpacingBefore(10);
        terms.add(new Chunk("Terms & Conditions\n", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        terms.add(new Chunk(invoiceTerms, FontFactory.getFont(FontFactory.HELVETICA, 8)));
        document.add(terms);

        // Add thank you message
        Paragraph thankYou = new Paragraph("Thank you for your business!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11));
        thankYou.setAlignment(Element.ALIGN_CENTER);
        thankYou.setSpacingBefore(15);
        document.add(thankYou);
    }
}