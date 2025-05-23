package tn.esprit.microservice1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lesson")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 5000)
    private String content;
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @Column(name = "video_type", length = 20)
    private String videoType; // "youtube", "vimeo", "url", etc.
    
    @Column(name = "pdf_url", columnDefinition = "LONGTEXT")
    private String pdfUrl;
    
    @Column(name = "pdf_name", length = 255)
    private String pdfName;
    
    @Column(name = "presentation_url", columnDefinition = "LONGTEXT")
    private String presentationUrl;
    
    @Column(name = "presentation_name", length = 255)
    private String presentationName;
    
    @Column(name = "converted_presentation_url", columnDefinition = "LONGTEXT")
    private String convertedPresentationUrl;
    
    @Column(name = "presentation_conversion_status", length = 20)
    private String presentationConversionStatus; // "PENDING", "COMPLETED", "FAILED"
    
    @Column(name = "presentation_html_content", columnDefinition = "LONGTEXT")
    private String presentationHtmlContent;
    
    @Column(name = "presentation_image_path", length = 500)
    private String presentationImagePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Course course;

    @Column(name = "order_in_course")
    private Integer orderInCourse;
}