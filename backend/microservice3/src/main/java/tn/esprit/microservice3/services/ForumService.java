package tn.esprit.microservice3.services;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.microservice3.DTO.ForumDTO;
import tn.esprit.microservice3.entities.Forum;
import tn.esprit.microservice3.repositories.ForumRepo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ForumService {
    private final ForumRepo forumRepo;

    public List<ForumDTO> getAllForums() {
        return forumRepo.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ForumDTO getForumById(int idForum) {
        Forum forum = forumRepo.findById(idForum)
                .orElseThrow(() -> new RuntimeException("Forum not found"));
        return mapToDTO(forum);
    }

    public Forum createForum(Forum forum) {
        return forumRepo.save(forum);
    }

    public Forum updateForum(int idForum, Forum forum) {
        Forum existingForum = forumRepo.findById(idForum)
                .orElseThrow(() -> new RuntimeException("Forum not found"));
        existingForum.setTitle(forum.getTitle());
        existingForum.setDescription(forum.getDescription());
        existingForum.setDateCreation(forum.getDateCreation());
        existingForum.setNbrPosts(forum.getNbrPosts());
        return forumRepo.save(existingForum);
    }

    public void deleteForumById(int idForum) {
        forumRepo.deleteById(idForum);
    }

    public List<Forum> getForumsByDate(LocalDate date) {
        return forumRepo.findByDateCreation(date);
    }

    private ForumDTO mapToDTO(Forum forum) {
        ForumDTO dto = new ForumDTO();
        dto.setIdForum(forum.getIdForum());
        dto.setTitle(forum.getTitle());
        dto.setDescription(forum.getDescription());
        dto.setDateCreation(forum.getDateCreation());
        dto.setNbrPosts(forum.getNbrPosts());
        return dto;
    }


}