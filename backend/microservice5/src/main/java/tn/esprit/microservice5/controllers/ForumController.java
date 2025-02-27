package tn.esprit.microservice5.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.entities.Forum;
import tn.esprit.microservice5.services.ForumService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/forums")
@RequiredArgsConstructor
public class ForumController {
    private final ForumService forumService;

    @GetMapping("/all")
    public List<Forum> getAllForums() {
        return forumService.getAllForums();
    }

    @PostMapping("/create")
    public Forum createForum(@RequestBody Forum forum) {
        return forumService.createForum(forum);
    }

    @PutMapping("/update/{idForum}")
    public Forum updateForum(@PathVariable int idForum, @RequestBody Forum forum) {
        return forumService.updateForum(idForum, forum);
    }

    @DeleteMapping("/delete/{idForum}")
    public void deleteForum(@PathVariable int idForum) {
        forumService.deleteForumById(idForum);
    }

    @GetMapping("/byDate")
    public List<Forum> getForumsByDate(@RequestParam LocalDateTime date) {
        return forumService.getForumsByDate(date);
    }
}