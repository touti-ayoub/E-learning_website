package tn.esprit.microservice3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice3.DTO.ForumDTO;
import tn.esprit.microservice3.entities.Forum;
import tn.esprit.microservice3.services.ForumService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/forums")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/all")
    public List<ForumDTO> getAllForums() {
        return forumService.getAllForums();
    }

    @PostMapping("/create")
    public Forum createForum(@Valid @RequestBody Forum forum) {
        return forumService.createForum(forum);
    }

    @PutMapping("/update/{idForum}")
    public Forum updateForum(@PathVariable int idForum, @Valid @RequestBody Forum forum) {
        return forumService.updateForum(idForum, forum);
    }

    @DeleteMapping("/delete/{idForum}")
    public void deleteForum(@PathVariable int idForum) {
        forumService.deleteForumById(idForum);
    }

    @GetMapping("/byDate")
    public List<Forum> getForumsByDate(@RequestParam LocalDate date) {
        return forumService.getForumsByDate(date);
    }

    @GetMapping("/{idForum}")
    public ForumDTO getForumById(@PathVariable int idForum) {
        return forumService.getForumById(idForum);
    }

}