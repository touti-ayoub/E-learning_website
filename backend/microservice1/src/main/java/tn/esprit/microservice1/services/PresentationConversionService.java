package tn.esprit.microservice1.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.repositories.LessonRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class PresentationConversionService {

    private final LessonRepository lessonRepository;
    
    @Value("${app.upload.dir:${user.home}/uploads/presentations}")
    private String uploadDir;
    
    public PresentationConversionService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }
    
    @Async
    public void convertPresentation(Long lessonId) {
        log.info("Starting conversion for lesson ID: {}", lessonId);
        
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with ID: " + lessonId));
        
        if (lesson.getPresentationUrl() == null || lesson.getPresentationUrl().isEmpty()) {
            log.warn("No presentation URL found for lesson ID: {}", lessonId);
            return;
        }
        
        try {
            // Update status to PENDING
            lesson.setPresentationConversionStatus("PENDING");
            lessonRepository.save(lesson);
            
            // Process the presentation
            String htmlContent;
            
            if (lesson.getPresentationUrl().startsWith("data:")) {
                // Process base64 encoded presentation
                htmlContent = processBase64Presentation(lesson);
            } else {
                // Process URL presentation
                htmlContent = processUrlPresentation(lesson);
            }
            
            // Update the lesson with the converted content
            lesson.setPresentationHtmlContent(htmlContent);
            lesson.setPresentationConversionStatus("COMPLETED");
            lessonRepository.save(lesson);
            
            log.info("Conversion completed for lesson ID: {}", lessonId);
        } catch (Exception e) {
            log.error("Error converting presentation for lesson ID: {}", lessonId, e);
            lesson.setPresentationConversionStatus("FAILED");
            lessonRepository.save(lesson);
        }
    }
    
    private String processBase64Presentation(Lesson lesson) throws Exception {
        log.info("Processing base64 presentation for lesson: {}", lesson.getId());
        
        // Extract the base64 content
        String base64Content = lesson.getPresentationUrl().split(",")[1];
        byte[] decodedBytes = Base64.decodeBase64(base64Content);
        
        // Create a temporary file
        String fileName = UUID.randomUUID().toString() + ".pptx";
        File tempDir = new File(uploadDir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        Path tempFilePath = Paths.get(uploadDir, fileName);
        Files.write(tempFilePath, decodedBytes);
        
        // Convert to HTML with images
        String html = convertPptxToHtml(tempFilePath.toFile(), lesson.getId());
        
        // Clean up temp file
        Files.deleteIfExists(tempFilePath);
        
        return html;
    }
    
    private String processUrlPresentation(Lesson lesson) throws Exception {
        log.info("Processing URL presentation for lesson: {}", lesson.getId());
        
        // Download the file
        String fileName = UUID.randomUUID().toString() + ".pptx";
        File tempDir = new File(uploadDir);
        if (!tempDir.exists()) {
            tempDir.mkdirs();
        }
        
        Path tempFilePath = Paths.get(uploadDir, fileName);
        
        try (InputStream in = new java.net.URL(lesson.getPresentationUrl()).openStream()) {
            Files.copy(in, tempFilePath);
        }
        
        // Convert to HTML with images
        String html = convertPptxToHtml(tempFilePath.toFile(), lesson.getId());
        
        // Clean up temp file
        Files.deleteIfExists(tempFilePath);
        
        return html;
    }
    
    private String convertPptxToHtml(File pptxFile, Long lessonId) throws Exception {
        log.info("Converting PPTX to HTML for lesson ID: {}", lessonId);
        
        // Create image output directory
        String imagesDir = uploadDir + "/images/" + lessonId;
        new File(imagesDir).mkdirs();
        
        // Generate a unique prefix for this conversion
        String prefix = UUID.randomUUID().toString().substring(0, 8);
        
        // Build HTML content
        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html lang=\"en\">\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("  <meta charset=\"UTF-8\">\n");
        htmlBuilder.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        htmlBuilder.append("  <title>").append(StringUtils.hasText(pptxFile.getName()) ? pptxFile.getName() : "Presentation").append("</title>\n");
        htmlBuilder.append("  <style>\n");
        htmlBuilder.append("    .slide-container { max-width: 100%; margin: 0 auto; }\n");
        htmlBuilder.append("    .slide { display: none; text-align: center; }\n");
        htmlBuilder.append("    .slide.active { display: block; }\n");
        htmlBuilder.append("    .slide-image { max-width: 100%; height: auto; border: 1px solid #ddd; }\n");
        htmlBuilder.append("    .slide-nav { text-align: center; margin: 15px 0; }\n");
        htmlBuilder.append("    .slide-nav button { padding: 5px 15px; margin: 0 5px; background-color: #06BBCC; color: white; border: none; border-radius: 4px; cursor: pointer; }\n");
        htmlBuilder.append("    .slide-counter { display: block; margin-top: 10px; color: #666; }\n");
        htmlBuilder.append("  </style>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("  <div class=\"slide-container\">\n");
        
        try (FileInputStream inputStream = new FileInputStream(pptxFile)) {
            XMLSlideShow ppt = new XMLSlideShow(inputStream);
            Dimension dimension = ppt.getPageSize();
            
            List<XSLFSlide> slides = ppt.getSlides();
            List<String> imageNames = new ArrayList<>();
            
            for (int i = 0; i < slides.size(); i++) {
                XSLFSlide slide = slides.get(i);
                
                // Create a BufferedImage to render the slide
                BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics = image.createGraphics();
                
                // Fill with white background
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                graphics.setPaint(Color.WHITE);
                graphics.fill(new Rectangle2D.Float(0, 0, dimension.width, dimension.height));
                
                // Draw the slide
                slide.draw(graphics);
                
                // Save as PNG
                String imgName = prefix + "-slide-" + (i + 1) + ".png";
                imageNames.add(imgName);
                File imgFile = new File(imagesDir, imgName);
                ImageIO.write(image, "png", imgFile);
                
                // Add to HTML with absolute URL
                String activeClass = (i == 0) ? " active" : "";
                htmlBuilder.append("    <div class=\"slide").append(activeClass).append("\">\n");
                htmlBuilder.append("      <img class=\"slide-image\" src=\"/api/lessons/").append(lessonId)
                          .append("/presentation/images/").append(i + 1).append("/").append(imgName)
                          .append("\" alt=\"Slide ").append(i + 1).append("\">\n");
                htmlBuilder.append("    </div>\n");
                
                graphics.dispose();
            }
            
            // Store image names in lesson for later retrieval
            Lesson lesson = lessonRepository.findById(lessonId)
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
            lesson.setConvertedPresentationUrl(String.join(",", imageNames));
            
            // Set the image path pattern for later retrieval
            // This pattern can be used to construct the actual file path by replacing the placeholders
            String imagePathPattern = imagesDir + "/{slideId}/" + prefix + "-slide-{slideId}.png";
            lesson.setPresentationImagePath(imagePathPattern);
            
            lessonRepository.save(lesson);
            
            // Add navigation controls
            htmlBuilder.append("    <div class=\"slide-nav\">\n");
            htmlBuilder.append("      <button id=\"prevSlide\">Previous</button>\n");
            htmlBuilder.append("      <button id=\"nextSlide\">Next</button>\n");
            htmlBuilder.append("      <span class=\"slide-counter\">Slide <span id=\"currentSlide\">1</span> of ").append(slides.size()).append("</span>\n");
            htmlBuilder.append("    </div>\n");
            
            // Add JavaScript for navigation
            htmlBuilder.append("  </div>\n");
            htmlBuilder.append("  <script>\n");
            htmlBuilder.append("    document.addEventListener('DOMContentLoaded', function() {\n");
            htmlBuilder.append("      const slides = document.querySelectorAll('.slide');\n");
            htmlBuilder.append("      const prevBtn = document.getElementById('prevSlide');\n");
            htmlBuilder.append("      const nextBtn = document.getElementById('nextSlide');\n");
            htmlBuilder.append("      const currentSlideSpan = document.getElementById('currentSlide');\n");
            htmlBuilder.append("      let currentSlide = 0;\n");
            htmlBuilder.append("\n");
            htmlBuilder.append("      function showSlide(index) {\n");
            htmlBuilder.append("        slides.forEach(slide => slide.classList.remove('active'));\n");
            htmlBuilder.append("        slides[index].classList.add('active');\n");
            htmlBuilder.append("        currentSlideSpan.textContent = index + 1;\n");
            htmlBuilder.append("      }\n");
            htmlBuilder.append("\n");
            htmlBuilder.append("      prevBtn.addEventListener('click', function() {\n");
            htmlBuilder.append("        currentSlide = (currentSlide > 0) ? currentSlide - 1 : slides.length - 1;\n");
            htmlBuilder.append("        showSlide(currentSlide);\n");
            htmlBuilder.append("      });\n");
            htmlBuilder.append("\n");
            htmlBuilder.append("      nextBtn.addEventListener('click', function() {\n");
            htmlBuilder.append("        currentSlide = (currentSlide < slides.length - 1) ? currentSlide + 1 : 0;\n");
            htmlBuilder.append("        showSlide(currentSlide);\n");
            htmlBuilder.append("      });\n");
            htmlBuilder.append("\n");
            htmlBuilder.append("      // Keyboard navigation\n");
            htmlBuilder.append("      document.addEventListener('keydown', function(e) {\n");
            htmlBuilder.append("        if (e.key === 'ArrowLeft') prevBtn.click();\n");
            htmlBuilder.append("        if (e.key === 'ArrowRight') nextBtn.click();\n");
            htmlBuilder.append("      });\n");
            htmlBuilder.append("    });\n");
            htmlBuilder.append("  </script>\n");
            htmlBuilder.append("</body>\n");
            htmlBuilder.append("</html>");
        }
        
        String htmlContent = htmlBuilder.toString();
        
        // Update lesson with HTML content
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found"));
        lesson.setPresentationHtmlContent(htmlContent);
        lessonRepository.save(lesson);
        
        return htmlContent;
    }
} 