package tn.esprit.microservice5.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice5.entities.Post;
import tn.esprit.microservice5.services.PostService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/mic5/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    @PostMapping("/create")
    public Post createPost(@RequestBody Post post) {
        return postService.createPost(post);
    }

    @PutMapping("/update/{idPost}")
    public Post updatePost(@PathVariable int idPost, @RequestBody Post post) {
        return postService.updatePost(idPost, post);
    }

    @DeleteMapping("/delete/{postId}")
    public void deletePost(@PathVariable Integer postId) {
        postService.deletePostById(postId);
    }

    @GetMapping("/byDate")
    public List<Post> getPostsByDate(@RequestParam LocalDateTime date) {
        return postService.getPostsByDate(date);
    }

}