package tn.esprit.microservice3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice3.DTO.InteractionDTO;
import tn.esprit.microservice3.entities.Interaction;
import tn.esprit.microservice3.services.InteractionService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/interactions")
@RequiredArgsConstructor
public class InteractionController {
    private final InteractionService interactionService;

    @GetMapping("/all")
    public List<InteractionDTO> getAllInteractions() {
        return interactionService.getAllInteractions();
    }

    @PostMapping("/create")
    public Interaction createInteraction(@Valid @RequestBody Interaction interaction) {
        return interactionService.createInteraction(interaction);
    }

    @PutMapping("/update/{idInteraction}")
    public Interaction updateInteraction(@PathVariable int idInteraction, @Valid @RequestBody Interaction interaction) {
        return interactionService.updateInteraction(idInteraction, interaction);
    }

    @DeleteMapping("/delete/{idInteraction}")
    public void deleteInteraction(@PathVariable int idInteraction) {
        interactionService.deleteInteractionById(idInteraction);
    }
    @GetMapping("/{idInteraction}")
    public InteractionDTO getInteractionById(@PathVariable int idInteraction) {
        return interactionService.getInteractionById(idInteraction);
    }
}