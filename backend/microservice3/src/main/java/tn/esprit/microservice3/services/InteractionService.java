package tn.esprit.microservice3.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice3.DTO.InteractionDTO;
import tn.esprit.microservice3.entities.Interaction;
import tn.esprit.microservice3.entities.InteractionType;
import tn.esprit.microservice3.repositories.InteractionRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InteractionService {
    private final InteractionRepo interactionRepo;

    public List<InteractionDTO> getAllInteractions() {
        return interactionRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public InteractionDTO getInteractionById(int idInteraction) {
        Interaction interaction = interactionRepo.findById(idInteraction)
                .orElseThrow(() -> new RuntimeException("Interaction not found"));
        return mapToDTO(interaction);
    }

    public Interaction createInteraction(Interaction interaction) {
        return interactionRepo.save(interaction);
    }

    public Interaction updateInteraction(int idInteraction, Interaction interaction) {
        Interaction existingInteraction = interactionRepo.findById(idInteraction)
                .orElseThrow(() -> new RuntimeException("Interaction not found"));
        existingInteraction.setContentInteraction(interaction.getContentInteraction());
        existingInteraction.setDateInteraction(interaction.getDateInteraction());
        // Update other fields as necessary
        return interactionRepo.save(existingInteraction);
    }

    public void deleteInteractionById(int idInteraction) {
        interactionRepo.deleteById(idInteraction);
    }

    private InteractionDTO mapToDTO(Interaction interaction) {
        InteractionDTO dto = new InteractionDTO();
        dto.setIdInteraction(interaction.getIdInteraction());
        dto.setContentInteraction(interaction.getContentInteraction());
        dto.setPostId(interaction.getPost().getIdPost());
        return dto;
    }
    public Interaction addLikeOrDislike(int postId, Interaction interaction, InteractionType type) {
        interaction.setTypeInteraction(type);
        interaction.setDateInteraction(LocalDate.now());
        // Associez le post ici (par exemple, via un PostService)
        return interactionRepo.save(interaction);
    }
}