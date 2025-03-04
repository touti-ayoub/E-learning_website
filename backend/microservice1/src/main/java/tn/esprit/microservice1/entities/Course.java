package tn.esprit.microservice1.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(nullable = false)
    private String category;

    @Enumerated(EnumType.STRING)
    private CourseStatus status = CourseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private CourseType type;

    @Column(nullable = false)
    private Double price;

    private Integer duration;

    @Column(nullable = false)
    private String language;

    private String level;

    @Column(name = "estimated_completion_time")
    private Integer estimatedCompletionTime;

    @Column(name = "target_audience", length = 1000)
    private String targetAudience;

    @Column(name = "is_automated")
    private boolean isAutomated = false;

    @Embedded
    private AutomatedFeatures automatedFeatures;

    @Column(name = "difficulty_score")
    private Double difficultyScore;

    @Column(name = "engagement_score")
    private Double engagementScore;

    @Column(name = "completion_rate")
    private Double completionRate;

    @Column(name = "recommendation_tags", length = 1000)
    private String recommendationTags;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private User instructor;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseModule> modules = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "enrollments",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> enrolledUsers = new HashSet<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseProgress> userProgress = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "course_ai_generated_tags")
    @Column(name = "tag")
    private List<String> aiGeneratedTags = new ArrayList<>();

    @Column(name = "ai_generated_summary", length = 2000)
    private String aiGeneratedSummary;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LearningObjective> learningObjectives = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrerequisiteSkill> prerequisites = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "course_skill_weights")
    @MapKeyColumn(name = "skill_name")
    @Column(name = "weight")
    private Map<String, Double> skillWeights = new HashMap<>();

    public Course() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = CourseStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
        return difficultyScore;
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

    public List<String> getAiGeneratedTags() {
        return aiGeneratedTags;
    }

    public void setAiGeneratedTags(List<String> aiGeneratedTags) {
        this.aiGeneratedTags = aiGeneratedTags;
    }

    public String getAiGeneratedSummary() {
        return aiGeneratedSummary;
    }

    public void setAiGeneratedSummary(String aiGeneratedSummary) {
        this.aiGeneratedSummary = aiGeneratedSummary;
    }

    public List<LearningObjective> getLearningObjectives() {
        return learningObjectives;
    }

    public void setLearningObjectives(List<LearningObjective> learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    public List<PrerequisiteSkill> getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(List<PrerequisiteSkill> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public Map<String, Double> getSkillWeights() {
        return skillWeights;
    }

    public void setSkillWeights(Map<String, Double> skillWeights) {
        this.skillWeights = skillWeights;
    }
}
