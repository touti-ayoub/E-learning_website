package tn.esprit.microservice1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Entity
@Table(name = "category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "cover_image", nullable = true, columnDefinition = "MEDIUMBLOB")
    @Lob
    private byte[] coverImage;
    
    @Transient
    @JsonProperty("coverImageData")
    private String coverImageBase64;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Course> courses = new ArrayList<>();
    
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
}