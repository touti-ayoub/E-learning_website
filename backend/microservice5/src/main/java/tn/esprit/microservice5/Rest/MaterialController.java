package tn.esprit.microservice5.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.DTO.MaterialDTO;
import tn.esprit.microservice5.Service.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @GetMapping
    public ResponseEntity<List<MaterialDTO>> getAllMaterials() {
        List<MaterialDTO> materials = materialService.getAllMaterials();
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterialDTO> getMaterialById(@PathVariable long id) {
        MaterialDTO material = materialService.getMaterialById(id);
        return material != null ? ResponseEntity.ok(material) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<MaterialDTO> createMaterial(@RequestBody MaterialDTO materialDTO) {
        MaterialDTO createdMaterial = materialService.createMaterial(materialDTO);
        return ResponseEntity.ok(createdMaterial);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaterialDTO> updateMaterial(@PathVariable long id, @RequestBody MaterialDTO materialDTO) {
        MaterialDTO updatedMaterial = materialService.updateMaterial(id, materialDTO);
        return updatedMaterial != null ? ResponseEntity.ok(updatedMaterial) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMaterial(@PathVariable long id) {
        materialService.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}