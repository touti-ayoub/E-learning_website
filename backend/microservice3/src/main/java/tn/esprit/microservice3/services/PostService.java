package tn.esprit.microservice3.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice3.DTO.PostDTO;
import tn.esprit.microservice3.entities.Forum;
import tn.esprit.microservice3.entities.Post;
import tn.esprit.microservice3.repositories.ForumRepo;
import tn.esprit.microservice3.repositories.PostRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepo postRepo;
    private final ForumRepo forumRepo;

    public List<PostDTO> getAllPosts() {
        return postRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PostDTO getPostById(int idPost) {
        Post post = postRepo.findById(idPost)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return mapToDTO(post);
    }

    public Post updatePost(int idPost, Post post) {
        Post existingPost = postRepo.findById(idPost)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        existingPost.setContent(post.getContent());
        existingPost.setDatePost(post.getDatePost());
        return postRepo.save(existingPost);
    }

    public void deletePostById(Integer postId) {
        postRepo.deleteById(postId);
    }

    public List<Post> getPostsByDate(LocalDate date) {
        return postRepo.findByDatePost(date);
    }

    public Post addPostToForum(Integer forumId, Post post) {
        Forum forum = forumRepo.findById(forumId)
                .orElseThrow(() -> new RuntimeException("Forum not found"));
        post.setForum(forum);
        return postRepo.save(post);
    }

    private PostDTO mapToDTO(Post post) {
        PostDTO dto = new PostDTO();
        dto.setIdPost(post.getIdPost());
        dto.setContent(post.getContent());
        dto.setDatePost(post.getDatePost());
        dto.setForumId(post.getForum().getIdForum());
        return dto;
    }

    public List<PostDTO> getPostsByForum(int forumId) {
        return postRepo.findByForum_IdForum(forumId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}