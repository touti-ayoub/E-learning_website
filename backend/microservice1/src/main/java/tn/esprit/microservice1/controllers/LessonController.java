package tn.esprit.microservice1.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.microservice1.entities.Lesson;
import tn.esprit.microservice1.services.CourseAccessClientService;
import tn.esprit.microservice1.services.LessonService;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final LessonService lessonService;
    private final CourseAccessClientService courseAccessService;
    private final Logger logger = LoggerFactory.getLogger(LessonController.class);
    
    @Value("${app.upload.dir:${user.home}/uploads/presentations}")
    private String uploadDir;

    public LessonController(LessonService lessonService, CourseAccessClientService courseAccessService) {
        this.lessonService = lessonService;
        this.courseAccessService = courseAccessService;
    }

    /**
     * Get a lesson by its ID
     *
     * @param id The lesson ID
     * @param userId Optional user ID for access control
     * @return The lesson if found and accessible
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLessonById(
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userId) {
        try {
            Lesson lesson = lessonService.getLessonById(id);
            
            // Check course access if userId is provided
            if (userId != null && lesson.getCourse() != null) {
                Long courseId = lesson.getCourse().getId();
                
                // If course is paid, check if user has access
                if (!courseAccessService.isCoursePublic(courseId)) {
                    if (!courseAccessService.checkCourseAccess(userId, courseId)) {
                        logger.warn("Access denied: User {} does not have access to course {}", userId, courseId);
                        return new ResponseEntity<>(Map.of(
                            "error", "Access denied",
                            "message", "You need to purchase this course to access its content",
                            "courseId", courseId
                        ), HttpStatus.FORBIDDEN);
                    }
                }
            }
            
            return ResponseEntity.ok(lesson);
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error retrieving lesson", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Create a new lesson for a course
     *
     * @param lesson The lesson to create
     * @param courseId The ID of the course to which this lesson belongs
     * @return The created lesson
     */
    @PostMapping("/{courseId}")
    public ResponseEntity<?> createLesson(
            @RequestBody Lesson lesson,
            @PathVariable Long courseId) {
        try {
            logger.info("Creating lesson with title: {} for course: {}", lesson.getTitle(), courseId);
            Lesson createdLesson = lessonService.createLesson(lesson, courseId);
            return new ResponseEntity<>(createdLesson, HttpStatus.CREATED);
        } catch (NoSuchElementException e) {
            logger.error("Course not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error creating lesson", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Update an existing lesson
     *
     * @param id The lesson ID to update
     * @param lesson The updated lesson data
     * @return The updated lesson
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLesson(
            @PathVariable Long id,
            @RequestBody Lesson lesson) {
        try {
            logger.info("Updating lesson with ID: {}", id);
            Lesson updatedLesson = lessonService.updateLesson(id, lesson);
            return ResponseEntity.ok(updatedLesson);
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error updating lesson", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Delete a lesson
     *
     * @param id The lesson ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable Long id) {
        try {
            logger.info("Deleting lesson with ID: {}", id);
            lessonService.deleteLesson(id);
            return ResponseEntity.ok(Map.of("message", "Lesson deleted successfully"));
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error deleting lesson", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Upload a presentation for a lesson
     *
     * @param id The lesson ID
     * @param file The presentation file to upload
     * @return The updated lesson with presentation data
     */
    @PostMapping(value = "/{id}/presentation", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPresentation(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "Presentation file is empty"), HttpStatus.BAD_REQUEST);
            }
            
            logger.info("Uploading presentation for lesson ID: {}, filename: {}", id, file.getOriginalFilename());
            Lesson updatedLesson = lessonService.uploadPresentation(id, file);
            return ResponseEntity.ok(updatedLesson);
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            logger.error("Error processing file upload", e);
            return new ResponseEntity<>(Map.of("error", "Failed to process file upload: " + e.getMessage()), 
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error uploading presentation", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download a presentation - Access controlled
     */
    @GetMapping(value = "/{id}/presentation/download", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> downloadPresentation(
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userId) {
        try {
            Lesson lesson = lessonService.getLessonById(id);
            
            // Check course access if userId is provided
            if (userId != null && lesson.getCourse() != null) {
                Long courseId = lesson.getCourse().getId();
                
                // If course is paid, check if user has access
                if (!courseAccessService.isCoursePublic(courseId)) {
                    if (!courseAccessService.checkCourseAccess(userId, courseId)) {
                        logger.warn("Access denied: User {} does not have access to course {}", userId, courseId);
                        return new ResponseEntity<>(Map.of(
                            "error", "Access denied",
                            "message", "You need to purchase this course to access its content",
                            "courseId", courseId
                        ), HttpStatus.FORBIDDEN);
                    }
                }
            }
            
            if (lesson.getPresentationUrl() == null || lesson.getPresentationUrl().isEmpty()) {
                return new ResponseEntity<>(Map.of("error", "No presentation available for this lesson"), HttpStatus.NOT_FOUND);
            }
            
            String fileName = lesson.getPresentationName() != null && !lesson.getPresentationName().isEmpty() ?
                              lesson.getPresentationName() : "presentation.pptx";
            
            // If the presentation is stored as base64, decode it
            if (lesson.getPresentationUrl().startsWith("data:")) {
                // Extract the base64 part (after the comma)
                String base64Data = lesson.getPresentationUrl().split(",")[1];
                byte[] presentationData = Base64.getDecoder().decode(base64Data);
                
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                        .body(presentationData);
            } else {
                // If it's a URL, redirect to it for download
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, lesson.getPresentationUrl())
                        .build();
            }
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error downloading presentation", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Trigger the conversion of a presentation to HTML
     *
     * @param id The lesson ID
     * @return Status of the conversion request
     */
    @PostMapping("/{id}/presentation/convert")
    public ResponseEntity<?> convertPresentation(@PathVariable Long id) {
        try {
            logger.info("Converting presentation for lesson ID: {}", id);
            boolean started = lessonService.triggerPresentationConversion(id);
            
            if (!started) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "No presentation found for this lesson"
                ));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Conversion process started"
            ));
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error converting presentation", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Get the conversion status of a presentation
     *
     * @param id The lesson ID
     * @return The conversion status
     */
    @GetMapping("/{id}/presentation/status")
    public ResponseEntity<?> getConversionStatus(@PathVariable Long id) {
        try {
            logger.info("Getting conversion status for lesson ID: {}", id);
            String status = lessonService.getPresentationConversionStatus(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("lessonId", id);
            response.put("status", status);
            
            if ("COMPLETED".equals(status)) {
                response.put("converted", true);
                response.put("viewUrl", "/api/lessons/" + id + "/presentation/view");
            } else {
                response.put("converted", false);
            }
            
            return ResponseEntity.ok(response);
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error getting conversion status", e);
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * View presentation HTML - Access controlled
     */
    @GetMapping(value = "/{id}/presentation/view", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> viewPresentationHtml(
            @PathVariable Long id,
            @RequestParam(name = "userId", required = false) Long userId) {
        try {
            Lesson lesson = lessonService.getLessonById(id);
            
            // Check course access if userId is provided
            if (userId != null && lesson.getCourse() != null) {
                Long courseId = lesson.getCourse().getId();
                
                // If course is paid, check if user has access
                if (!courseAccessService.isCoursePublic(courseId)) {
                    if (!courseAccessService.checkCourseAccess(userId, courseId)) {
                        logger.warn("Access denied: User {} does not have access to course {}", userId, courseId);
                        
                        // Return HTML with access denied message instead of JSON
                        String accessDeniedHtml = "<html><body><h1>Access Denied</h1>" +
                            "<p>You need to purchase this course to access its content.</p>" +
                            "<p><a href='/courses/" + courseId + "'>View Course</a></p></body></html>";
                        
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .contentType(MediaType.TEXT_HTML)
                            .body(accessDeniedHtml);
                    }
                }
            }
            
            // Check if converted presentation HTML content is available
            if (lesson.getPresentationHtmlContent() == null || lesson.getPresentationHtmlContent().isEmpty()) {
                // Check conversion status
                String status = lesson.getPresentationConversionStatus();
                
                if ("PENDING".equals(status)) {
                    return ResponseEntity.ok(
                        "<html><body><h1>Converting Presentation</h1>" +
                        "<p>Your presentation is currently being converted. Please try again in a few moments.</p>" +
                        "</body></html>"
                    );
                } else if ("FAILED".equals(status)) {
                    return ResponseEntity.ok(
                        "<html><body><h1>Conversion Failed</h1>" +
                        "<p>We couldn't convert this presentation to HTML format. Please download it instead.</p>" +
                        "</body></html>"
                    );
                } else {
                    // Not yet converted or unknown status
                    lessonService.triggerPresentationConversion(id);
                    return ResponseEntity.ok(
                        "<html><body><h1>Not Available</h1>" +
                        "<p>Presentation is not available in HTML format. Please try again later or contact support.</p>" +
                        "</body></html>"
                    );
                }
            }
            
            // Return the converted HTML content
            return ResponseEntity.ok(lesson.getPresentationHtmlContent());
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return new ResponseEntity<>(
                "<html><body><h1>Not Found</h1><p>The requested lesson could not be found.</p></body></html>", 
                HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error viewing presentation", e);
            return new ResponseEntity<>(
                "<html><body><h1>Error</h1><p>An error occurred while trying to view this presentation.</p></body></html>", 
                HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get image from the presentation - Access controlled
     */
    @GetMapping("/{id}/presentation/images/{slideId}/{imageName}")
    public ResponseEntity<byte[]> getPresentationImage(
            @PathVariable Long id,
            @PathVariable Integer slideId,
            @PathVariable String imageName,
            @RequestParam(name = "userId", required = false) Long userId) {
        try {
            Lesson lesson = lessonService.getLessonById(id);
            
            // Check course access if userId is provided
            if (userId != null && lesson.getCourse() != null) {
                Long courseId = lesson.getCourse().getId();
                
                // If course is paid, check if user has access
                if (!courseAccessService.isCoursePublic(courseId)) {
                    if (!courseAccessService.checkCourseAccess(userId, courseId)) {
                        logger.warn("Access denied: User {} does not have access to course {}", userId, courseId);
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                    }
                }
            }
            
            // If the lesson doesn't have an image path pattern, construct one
            String imagePath;
            if (lesson.getPresentationImagePath() == null || lesson.getPresentationImagePath().isEmpty()) {
                // Fallback to a standard path structure
                imagePath = uploadDir + "/images/" + id + "/" + slideId + "/" + imageName;
                logger.warn("No image path pattern available for lesson ID: {}, using fallback: {}", id, imagePath);
            } else {
                // Use the stored path pattern
                imagePath = lesson.getPresentationImagePath()
                        .replace("{slideId}", slideId.toString())
                        .replace("{imageName}", imageName);
            }
            
            File imageFile = new File(imagePath);
            
            if (!imageFile.exists() || !imageFile.isFile()) {
                logger.warn("Image file not found: {}", imagePath);
                return ResponseEntity.notFound().build();
            }
            
            byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
            
            MediaType mediaType;
            if (imageName.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (imageName.toLowerCase().endsWith(".jpg") || imageName.toLowerCase().endsWith(".jpeg")) {
                mediaType = MediaType.IMAGE_JPEG;
            } else if (imageName.toLowerCase().endsWith(".gif")) {
                mediaType = MediaType.IMAGE_GIF;
            } else {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }
            
            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .body(imageBytes);
        } catch (NoSuchElementException e) {
            logger.error("Lesson not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error retrieving presentation image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get the raw presentation data
     * 
     * @param id The lesson ID
     * @return The presentation data
     */
    @GetMapping(value = "/{id}/presentation/raw", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getRawPresentation(@PathVariable Long id) {
        try {
            Lesson lesson = lessonService.getLessonById(id);
            if (lesson == null || lesson.getPresentationUrl() == null) {
                return ResponseEntity.notFound().build();
            }

            // Extract the base64 data
            String base64Data = lesson.getPresentationUrl().split(",")[1];
            byte[] presentationData = Base64.getDecoder().decode(base64Data);

            return ResponseEntity.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"presentation.pptx\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(presentationData);
        } catch (Exception e) {
            logger.error("Error retrieving raw presentation", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
} 