package tn.esprit.microservice5.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice5.entities.Forum;
import tn.esprit.microservice5.repositories.ForumRepo;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumRepo forumRepo;

    public List<Forum> getAllForums() {
        return forumRepo.findAll();
    }

    public Forum createForum(Forum forum) {
        return forumRepo.save(forum);
    }

    public Forum updateForum(int idForum, Forum forum) {
        Forum existingForum = forumRepo.findById(idForum).orElseThrow(() -> new RuntimeException("Forum not found"));
        existingForum.setTitle(forum.getTitle());
        existingForum.setDescription(forum.getDescription());
        existingForum.setDateCreation(forum.getDateCreation());
        existingForum.setNbrPosts(forum.getNbrPosts());
        // Update other fields as necessary
        return forumRepo.save(existingForum);
    }

    public void deleteForumById(int idForum) {
        forumRepo.deleteById(idForum);
    }

    public List<Forum> getForumsByDate(LocalDateTime date) {
        return forumRepo.findByDateCreation(date);
    }
}