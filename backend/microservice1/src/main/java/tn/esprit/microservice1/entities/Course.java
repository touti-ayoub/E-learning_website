package tn.esprit.microservice1.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

@Entity
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "free", nullable = false, columnDefinition = "boolean default false")
    private Boolean free = false;

    @Column(name = "cover_image", nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] coverImage;

    @Transient
    @JsonProperty("coverImageData")
    private String coverImageBase64;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Category category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Lesson> lessons = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getCoverImageBase64() {
        if (coverImage != null && coverImage.length > 0) {
            return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(coverImage);
        }
        return null;
    }

    public void setCoverImageBase64(String coverImageBase64) {
        if (coverImageBase64 != null && coverImageBase64.startsWith("data:image")) {
            try {
                String base64Data = coverImageBase64.replaceFirst("^data:image/[^;]+;base64,", "");
                this.coverImage = Base64.getDecoder().decode(base64Data);
            } catch (IllegalArgumentException e) {
                this.coverImage = null;
            }
        } else {
            this.coverImage = null;
        }
    }
    
    // Helper method to add lessons to the course
    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        lesson.setCourse(this);
    }
    
    // Helper method to remove a lesson from the course
    public void removeLesson(Lesson lesson) {
        lessons.remove(lesson);
        lesson.setCourse(null);
    }
    
    // Getter for categoryId
    @Transient
    @JsonProperty("categoryId")
    public Long getCategoryId() {
        return category != null ? category.getId() : null;
    }
}