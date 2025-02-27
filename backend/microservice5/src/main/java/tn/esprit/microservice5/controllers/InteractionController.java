package tn.esprit.microservice5.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.entities.Interaction;
import tn.esprit.microservice5.services.InteractionService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/mic5/interactions")
@RequiredArgsConstructor
public class InteractionController {
    private final InteractionService interactionService;

    @GetMapping("/all")
    public List<Interaction> getAllInteractions() {
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
}