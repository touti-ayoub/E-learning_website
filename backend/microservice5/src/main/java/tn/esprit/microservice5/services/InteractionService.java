package tn.esprit.microservice5.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.entities.Interaction;
import tn.esprit.microservice5.repositories.InteractionRepo;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InteractionService {
    private final InteractionRepo interactionRepo;

    public List<Interaction> getAllInteractions() {
        return interactionRepo.findAll();
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
}