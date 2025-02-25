package tn.esprit.microservice5.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.entities.Post;
import tn.esprit.microservice5.repositories.PostRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepo postRepo;

    public void deletePostById(Integer postId) {
        postRepo.deleteById(postId);
    }
    public Post createPost(Post post) {
        return postRepo.save(post);
    }

    public Post updatePost(int idPost, Post post) {
        Post existingPost = postRepo.findById(idPost).orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.setContent(post.getContent());
        existingPost.setDatePost(post.getDatePost());
        // Update other fields as necessary
        return postRepo.save(existingPost);
    }
    public List<Post> getPostsByDate(LocalDateTime date) {
        return postRepo.findByDatePost(date);
    }

}
