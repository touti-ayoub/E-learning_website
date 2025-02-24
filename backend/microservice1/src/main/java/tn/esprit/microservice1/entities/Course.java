package tn.esprit.microservice1.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Course {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String title;
    private String description;
    private String coverImage;
    private String category;
    @Enumerated(EnumType.STRING)
    private CourseStatus status;
    @Enumerated(EnumType.STRING)
    private CourseType type;
    private Double price;
    private Integer duration;
    private String language;
    private String level;
    private Integer estimatedCompletionTime;
    private String prerequisites;
    private String learningObjectives;
    private String targetAudience;
    private boolean isAutomated;
    @Embedded
    private AutomatedFeatures automatedFeatures;
    private Double difficultyScore;
    private Double engagementScore;
    private Double completionRate;
    private String recommendationTags;
    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;
    @Column(
            name = "updated_at"
    )
    private LocalDateTime updatedAt;
    @ManyToOne
    @JoinColumn(
            name = "instructor_id"
    )
    private User instructor;
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.ALL}
    )
    private Set<CourseModule> modules = new HashSet();
    @ManyToMany
    @JoinTable(
            name = "enrollments",
            joinColumns = {@JoinColumn(
                    name = "course_id"
            )},
            inverseJoinColumns = {@JoinColumn(
                    name = "user_id"
            )}
    )
    private Set<User> enrolledUsers = new HashSet();
    @OneToMany(
            mappedBy = "course",
            cascade = {CascadeType.ALL}
    )
    private Set<CourseProgress> userProgress = new HashSet();

    public Course() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return this.coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public CourseType getType() {
        return this.type;
    }

    public void setType(CourseType type) {
        this.type = type;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getDuration() {
        return this.duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLevel() {
        return this.level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Integer getEstimatedCompletionTime() {
        return this.estimatedCompletionTime;
    }

    public void setEstimatedCompletionTime(Integer estimatedCompletionTime) {
        this.estimatedCompletionTime = estimatedCompletionTime;
    }

    public String getPrerequisites() {
        return this.prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getLearningObjectives() {
        return this.learningObjectives;
    }

    public void setLearningObjectives(String learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public String getTargetAudience() {
        return this.targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getRecommendationTags() {
        return this.recommendationTags;
    }

    public void setRecommendationTags(String recommendationTags) {
        this.recommendationTags = recommendationTags;
    }

    public Double getDifficultyScore() {
        return this.difficultyScore;
    }

    public void setDifficultyScore(Double difficultyScore) {
        this.difficultyScore = difficultyScore;
    }

    public Double getEngagementScore() {
        return this.engagementScore;
    }

    public void setEngagementScore(Double engagementScore) {
        this.engagementScore = engagementScore;
    }

    public Double getCompletionRate() {
        return this.completionRate;
    }

    public void setCompletionRate(Double completionRate) {
        this.completionRate = completionRate;
    }

    public User getInstructor() {
        return this.instructor;
    }

    public void setInstructor(User instructor) {
        this.instructor = instructor;
    }

    public CourseStatus getStatus() {
        return this.status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<CourseModule> getModules() {
        return this.modules;
    }

    public Set<CourseProgress> getUserProgress() {
        return this.userProgress;
    }

    public Set<User> getEnrolledUsers() {
        return this.enrolledUsers;
    }

    public boolean isAutomated() {
        return this.isAutomated;
    }

    public void setAutomated(boolean automated) {
        this.isAutomated = automated;
    }

    public AutomatedFeatures getAutomatedFeatures() {
        return this.automatedFeatures;
    }

    public void setAutomatedFeatures(AutomatedFeatures automatedFeatures) {
        this.automatedFeatures = automatedFeatures;
    }
}
