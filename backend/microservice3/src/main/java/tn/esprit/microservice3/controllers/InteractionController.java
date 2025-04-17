package tn.esprit.microservice3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice3.DTO.InteractionDTO;
import tn.esprit.microservice3.entities.Interaction;
import tn.esprit.microservice3.entities.InteractionType;
import tn.esprit.microservice3.entities.Post;
import tn.esprit.microservice3.repositories.InteractionRepo;
import tn.esprit.microservice3.repositories.PostRepo;
import tn.esprit.microservice3.services.InteractionService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/interactions")
@RequiredArgsConstructor
public class InteractionController {
    private final InteractionService interactionService;
    private final InteractionRepo interactionRepo;
    private final PostRepo postRepository;

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

    @PostMapping("/like/{postId}")
    public Interaction likePost(@PathVariable int postId, @RequestBody Interaction interaction) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        interaction.setPost(post);
        interaction.setTypeInteraction(InteractionType.LIKE);

        // Increment the like count
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);

        return interactionRepo.save(interaction);
    }

    @PostMapping("/dislike/{postId}")
    public Interaction dislikePost(@PathVariable int postId, @RequestBody Interaction interaction) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));
        interaction.setPost(post);
        interaction.setTypeInteraction(InteractionType.DISLIKE);

        // Increment the dislike count
        post.setDislikeCount(post.getDislikeCount() + 1);
        postRepository.save(post);

        return interactionRepo.save(interaction);
    }
    @PostMapping("/comment/{postId}")
    public Interaction addComment(@PathVariable int postId, @RequestBody String contentInteraction) {
        System.out.println("Received comment content: " + contentInteraction);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // Filtrer les mots interdits
        String filteredContent = filterBadWords(contentInteraction);

        Interaction interaction = new Interaction();
        interaction.setPost(post);
        interaction.setTypeInteraction(InteractionType.COMMENT);
        interaction.setDateInteraction(LocalDate.now());
        interaction.setContentInteraction(filteredContent);

        try {
            return interactionRepo.save(interaction);
        } catch (Exception e) {
            System.err.println("Error saving interaction: " + e.getMessage());
            throw new RuntimeException("Failed to save interaction");
        }
    }
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int commentId) {
        Interaction interaction = interactionRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with ID: " + commentId));
        interactionRepo.delete(interaction);
        return ResponseEntity.noContent().build();
    }
    private static final List<String> BAD_WORDS =  List.of(
            // Français
            "con", "connard", "connasse", "enculé", "salope", "pute", "merde", "bordel", "chiant", "tafiole", "pd", "fdp", "batard", "nique", "branleur", "enculer",

            // Anglais
            "fuck", "shit", "bitch", "asshole", "bastard", "dick", "pussy", "cunt", "fucker", "motherfucker", "slut", "whore", "jerk", "idiot", "retard", "douchebag",

            // Tunisien (écriture latine courante)
            "zah",

            // Variantes phonétiques et SMS
            "f*ck", "sh*t", "n1que", "niq", "fck", "b!tch", "s@lope", "p*te", "m3rde"
    );

    private String filterBadWords(String content) {
        for (String badWord : BAD_WORDS) {
            if (content.toLowerCase().contains(badWord.toLowerCase())) {
                content = content.replaceAll("(?i)" + badWord, "***");
            }
        }
        return content;
    }
}