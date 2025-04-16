package tn.esprit.microservice3.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice3.DTO.PostDTO;
import tn.esprit.microservice3.entities.Post;
import tn.esprit.microservice3.services.PostService;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/all")
    public List<PostDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{idPost}")
    public PostDTO getPostById(@PathVariable int idPost) {
        return postService.getPostById(idPost);
    }
    @PostMapping("/create/{forumId}")
    public Post createPost(@PathVariable Integer forumId, @Valid @RequestBody Post post) {
        return postService.addPostToForum(forumId, post);
    }

    @PutMapping("/update/{idPost}")
    public Post updatePost(@PathVariable int idPost, @Valid @RequestBody Post post) {
        return postService.updatePost(idPost, post);
    }

    @DeleteMapping("/delete/{idPost}")
    public void deletePost(@PathVariable int idPost) {
        postService.deletePostById(idPost);
    }

    @GetMapping("/byDate")
    public List<Post> getPostsByDate(@RequestParam LocalDate date) {
        return postService.getPostsByDate(date);
    }

    @GetMapping("/byForum/{forumId}")
    public List<PostDTO> getPostsByForum(@PathVariable int forumId) {
        return postService.getPostsByForum(forumId);
    }
}