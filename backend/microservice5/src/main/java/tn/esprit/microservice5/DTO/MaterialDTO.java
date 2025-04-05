package tn.esprit.microservice5.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.microservice5.Model.Material;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialDTO {
    private long id;
    private long eventId;
    private String title;
    private String description;
    private String fileUrl;
    private String fileType;

    /**
     * Convert from Material entity to MaterialDTO
     */
    public static MaterialDTO fromEntity(Material material) {
        if (material == null) {
            return null;
        }

        return MaterialDTO.builder()
                .id(material.getId())
                .eventId(material.getEvent() != null ? material.getEvent().getEventId() : 0)
                .title(material.getTitle())
                .description(material.getDescription())
                .fileUrl(material.getFileUrl())
                .fileType(material.getFileType())
                .build();
    }
}