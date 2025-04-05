package tn.esprit.microservice5.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.DTO.MaterialDTO;
import tn.esprit.microservice5.Model.Event;
import tn.esprit.microservice5.Model.Material;
import tn.esprit.microservice5.Repo.IEventRepo;
import tn.esprit.microservice5.Repo.IMaterialRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaterialService {

    @Autowired
    private IMaterialRepo materialRepository;

    @Autowired
    private IEventRepo eventRepository;  // Make sure you have this injected

    public List<MaterialDTO> getAllMaterials() {
        return materialRepository.findAll().stream()
                .map(MaterialDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public MaterialDTO getMaterialById(long id) {
        Optional<Material> material = materialRepository.findById(id);
        return material.map(MaterialDTO::fromEntity).orElse(null);
    }

    public MaterialDTO createMaterial(MaterialDTO materialDTO) {
        // First fetch the event entity
        Event event = eventRepository.findById(materialDTO.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + materialDTO.getEventId()));

        Material material = new Material();
        material.setEvent(event);  // Set the event
        material.setTitle(materialDTO.getTitle());
        material.setDescription(materialDTO.getDescription());
        material.setFileUrl(materialDTO.getFileUrl());
        material.setFileType(materialDTO.getFileType());

        material = materialRepository.save(material);
        return MaterialDTO.fromEntity(material);
    }

    public MaterialDTO updateMaterial(long id, MaterialDTO materialDTO) {
        Optional<Material> materialOptional = materialRepository.findById(id);
        if (materialOptional.isPresent()) {
            Material material = materialOptional.get();
            // Update fields from DTO to entity
            material.setTitle(materialDTO.getTitle());
            material.setDescription(materialDTO.getDescription());
            material.setFileUrl(materialDTO.getFileUrl());
            material.setFileType(materialDTO.getFileType());
            // Save entity
            material = materialRepository.save(material);
            return MaterialDTO.fromEntity(material);
        }
        return null;
    }

    public void deleteMaterial(long id) {
        materialRepository.deleteById(id);
    }
}