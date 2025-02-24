package tn.esprit.microservice1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import lombok.Generated;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String title;
    private String description;
    @Column(
            columnDefinition = "TEXT"
    )
    private String content;
    private String videoUrl;
    private Integer orderIndex;
    private Integer duration;
    @Enumerated(EnumType.STRING)
    private LessonType type;
    @Column(
            columnDefinition = "TEXT"
    )
    private String objectives;
    @Column(
            columnDefinition = "TEXT"
    )
    private String resources;
    @Column(
            columnDefinition = "TEXT"
    )
    private String assignments;
    @Column(
            columnDefinition = "TEXT"
    )
    private String quizzes;
    @Column(
            columnDefinition = "TEXT"
    )
    private String exercises;
    private Double difficultyScore;
    @Column(
            columnDefinition = "TEXT"
    )
    private String prerequisites;
    @Column(
            columnDefinition = "TEXT"
    )
    private String recommendedPreparation;
    private Double averageCompletionTime;
    @Column(
            columnDefinition = "TEXT"
    )
    private String adaptivePath;
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
            name = "module_id"
    )
    @JsonIgnore
    private CourseModule module;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Generated
    public Long getId() {
        return this.id;
    }

    @Generated
    public String getTitle() {
        return this.title;
    }

    @Generated
    public String getDescription() {
        return this.description;
    }

    @Generated
    public String getContent() {
        return this.content;
    }

    @Generated
    public String getVideoUrl() {
        return this.videoUrl;
    }

    @Generated
    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    @Generated
    public Integer getDuration() {
        return this.duration;
    }

    @Generated
    public LessonType getType() {
        return this.type;
    }

    @Generated
    public String getObjectives() {
        return this.objectives;
    }

    @Generated
    public String getResources() {
        return this.resources;
    }

    @Generated
    public String getAssignments() {
        return this.assignments;
    }

    @Generated
    public String getQuizzes() {
        return this.quizzes;
    }

    @Generated
    public String getExercises() {
        return this.exercises;
    }

    @Generated
    public Double getDifficultyScore() {
        return this.difficultyScore;
    }

    @Generated
    public String getPrerequisites() {
        return this.prerequisites;
    }

    @Generated
    public String getRecommendedPreparation() {
        return this.recommendedPreparation;
    }

    @Generated
    public Double getAverageCompletionTime() {
        return this.averageCompletionTime;
    }

    @Generated
    public String getAdaptivePath() {
        return this.adaptivePath;
    }

    @Generated
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Generated
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @Generated
    public CourseModule getModule() {
        return this.module;
    }

    @Generated
    public void setId(final Long id) {
        this.id = id;
    }

    @Generated
    public void setTitle(final String title) {
        this.title = title;
    }

    @Generated
    public void setDescription(final String description) {
        this.description = description;
    }

    @Generated
    public void setContent(final String content) {
        this.content = content;
    }

    @Generated
    public void setVideoUrl(final String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Generated
    public void setOrderIndex(final Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Generated
    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    @Generated
    public void setType(final LessonType type) {
        this.type = type;
    }

    @Generated
    public void setObjectives(final String objectives) {
        this.objectives = objectives;
    }

    @Generated
    public void setResources(final String resources) {
        this.resources = resources;
    }

    @Generated
    public void setAssignments(final String assignments) {
        this.assignments = assignments;
    }

    @Generated
    public void setQuizzes(final String quizzes) {
        this.quizzes = quizzes;
    }

    @Generated
    public void setExercises(final String exercises) {
        this.exercises = exercises;
    }

    @Generated
    public void setDifficultyScore(final Double difficultyScore) {
        this.difficultyScore = difficultyScore;
    }

    @Generated
    public void setPrerequisites(final String prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Generated
    public void setRecommendedPreparation(final String recommendedPreparation) {
        this.recommendedPreparation = recommendedPreparation;
    }

    @Generated
    public void setAverageCompletionTime(final Double averageCompletionTime) {
        this.averageCompletionTime = averageCompletionTime;
    }

    @Generated
    public void setAdaptivePath(final String adaptivePath) {
        this.adaptivePath = adaptivePath;
    }

    @Generated
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Generated
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    @Generated
    public void setModule(final CourseModule module) {
        this.module = module;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Lesson)) {
            return false;
        } else {
            Lesson other = (Lesson)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label263: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label263;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label263;
                    }

                    return false;
                }

                Object this$orderIndex = this.getOrderIndex();
                Object other$orderIndex = other.getOrderIndex();
                if (this$orderIndex == null) {
                    if (other$orderIndex != null) {
                        return false;
                    }
                } else if (!this$orderIndex.equals(other$orderIndex)) {
                    return false;
                }

                label249: {
                    Object this$duration = this.getDuration();
                    Object other$duration = other.getDuration();
                    if (this$duration == null) {
                        if (other$duration == null) {
                            break label249;
                        }
                    } else if (this$duration.equals(other$duration)) {
                        break label249;
                    }

                    return false;
                }

                Object this$difficultyScore = this.getDifficultyScore();
                Object other$difficultyScore = other.getDifficultyScore();
                if (this$difficultyScore == null) {
                    if (other$difficultyScore != null) {
                        return false;
                    }
                } else if (!this$difficultyScore.equals(other$difficultyScore)) {
                    return false;
                }

                label235: {
                    Object this$averageCompletionTime = this.getAverageCompletionTime();
                    Object other$averageCompletionTime = other.getAverageCompletionTime();
                    if (this$averageCompletionTime == null) {
                        if (other$averageCompletionTime == null) {
                            break label235;
                        }
                    } else if (this$averageCompletionTime.equals(other$averageCompletionTime)) {
                        break label235;
                    }

                    return false;
                }

                Object this$title = this.getTitle();
                Object other$title = other.getTitle();
                if (this$title == null) {
                    if (other$title != null) {
                        return false;
                    }
                } else if (!this$title.equals(other$title)) {
                    return false;
                }

                label221: {
                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description == null) {
                            break label221;
                        }
                    } else if (this$description.equals(other$description)) {
                        break label221;
                    }

                    return false;
                }

                label214: {
                    Object this$content = this.getContent();
                    Object other$content = other.getContent();
                    if (this$content == null) {
                        if (other$content == null) {
                            break label214;
                        }
                    } else if (this$content.equals(other$content)) {
                        break label214;
                    }

                    return false;
                }

                Object this$videoUrl = this.getVideoUrl();
                Object other$videoUrl = other.getVideoUrl();
                if (this$videoUrl == null) {
                    if (other$videoUrl != null) {
                        return false;
                    }
                } else if (!this$videoUrl.equals(other$videoUrl)) {
                    return false;
                }

                label200: {
                    Object this$type = this.getType();
                    Object other$type = other.getType();
                    if (this$type == null) {
                        if (other$type == null) {
                            break label200;
                        }
                    } else if (this$type.equals(other$type)) {
                        break label200;
                    }

                    return false;
                }

                label193: {
                    Object this$objectives = this.getObjectives();
                    Object other$objectives = other.getObjectives();
                    if (this$objectives == null) {
                        if (other$objectives == null) {
                            break label193;
                        }
                    } else if (this$objectives.equals(other$objectives)) {
                        break label193;
                    }

                    return false;
                }

                Object this$resources = this.getResources();
                Object other$resources = other.getResources();
                if (this$resources == null) {
                    if (other$resources != null) {
                        return false;
                    }
                } else if (!this$resources.equals(other$resources)) {
                    return false;
                }

                Object this$assignments = this.getAssignments();
                Object other$assignments = other.getAssignments();
                if (this$assignments == null) {
                    if (other$assignments != null) {
                        return false;
                    }
                } else if (!this$assignments.equals(other$assignments)) {
                    return false;
                }

                label172: {
                    Object this$quizzes = this.getQuizzes();
                    Object other$quizzes = other.getQuizzes();
                    if (this$quizzes == null) {
                        if (other$quizzes == null) {
                            break label172;
                        }
                    } else if (this$quizzes.equals(other$quizzes)) {
                        break label172;
                    }

                    return false;
                }

                Object this$exercises = this.getExercises();
                Object other$exercises = other.getExercises();
                if (this$exercises == null) {
                    if (other$exercises != null) {
                        return false;
                    }
                } else if (!this$exercises.equals(other$exercises)) {
                    return false;
                }

                Object this$prerequisites = this.getPrerequisites();
                Object other$prerequisites = other.getPrerequisites();
                if (this$prerequisites == null) {
                    if (other$prerequisites != null) {
                        return false;
                    }
                } else if (!this$prerequisites.equals(other$prerequisites)) {
                    return false;
                }

                label151: {
                    Object this$recommendedPreparation = this.getRecommendedPreparation();
                    Object other$recommendedPreparation = other.getRecommendedPreparation();
                    if (this$recommendedPreparation == null) {
                        if (other$recommendedPreparation == null) {
                            break label151;
                        }
                    } else if (this$recommendedPreparation.equals(other$recommendedPreparation)) {
                        break label151;
                    }

                    return false;
                }

                Object this$adaptivePath = this.getAdaptivePath();
                Object other$adaptivePath = other.getAdaptivePath();
                if (this$adaptivePath == null) {
                    if (other$adaptivePath != null) {
                        return false;
                    }
                } else if (!this$adaptivePath.equals(other$adaptivePath)) {
                    return false;
                }

                label137: {
                    Object this$createdAt = this.getCreatedAt();
                    Object other$createdAt = other.getCreatedAt();
                    if (this$createdAt == null) {
                        if (other$createdAt == null) {
                            break label137;
                        }
                    } else if (this$createdAt.equals(other$createdAt)) {
                        break label137;
                    }

                    return false;
                }

                Object this$updatedAt = this.getUpdatedAt();
                Object other$updatedAt = other.getUpdatedAt();
                if (this$updatedAt == null) {
                    if (other$updatedAt != null) {
                        return false;
                    }
                } else if (!this$updatedAt.equals(other$updatedAt)) {
                    return false;
                }

                Object this$module = this.getModule();
                Object other$module = other.getModule();
                if (this$module == null) {
                    if (other$module == null) {
                        return true;
                    }
                } else if (this$module.equals(other$module)) {
                    return true;
                }

                return false;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof Lesson;
    }

    @Generated
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $orderIndex = this.getOrderIndex();
        result = result * 59 + ($orderIndex == null ? 43 : $orderIndex.hashCode());
        Object $duration = this.getDuration();
        result = result * 59 + ($duration == null ? 43 : $duration.hashCode());
        Object $difficultyScore = this.getDifficultyScore();
        result = result * 59 + ($difficultyScore == null ? 43 : $difficultyScore.hashCode());
        Object $averageCompletionTime = this.getAverageCompletionTime();
        result = result * 59 + ($averageCompletionTime == null ? 43 : $averageCompletionTime.hashCode());
        Object $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $content = this.getContent();
        result = result * 59 + ($content == null ? 43 : $content.hashCode());
        Object $videoUrl = this.getVideoUrl();
        result = result * 59 + ($videoUrl == null ? 43 : $videoUrl.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $objectives = this.getObjectives();
        result = result * 59 + ($objectives == null ? 43 : $objectives.hashCode());
        Object $resources = this.getResources();
        result = result * 59 + ($resources == null ? 43 : $resources.hashCode());
        Object $assignments = this.getAssignments();
        result = result * 59 + ($assignments == null ? 43 : $assignments.hashCode());
        Object $quizzes = this.getQuizzes();
        result = result * 59 + ($quizzes == null ? 43 : $quizzes.hashCode());
        Object $exercises = this.getExercises();
        result = result * 59 + ($exercises == null ? 43 : $exercises.hashCode());
        Object $prerequisites = this.getPrerequisites();
        result = result * 59 + ($prerequisites == null ? 43 : $prerequisites.hashCode());
        Object $recommendedPreparation = this.getRecommendedPreparation();
        result = result * 59 + ($recommendedPreparation == null ? 43 : $recommendedPreparation.hashCode());
        Object $adaptivePath = this.getAdaptivePath();
        result = result * 59 + ($adaptivePath == null ? 43 : $adaptivePath.hashCode());
        Object $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        Object $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        Object $module = this.getModule();
        result = result * 59 + ($module == null ? 43 : $module.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = String.valueOf(this.getId());
        return "Lesson(id=" + var10000 + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", content=" + this.getContent() + ", videoUrl=" + this.getVideoUrl() + ", orderIndex=" + String.valueOf(this.getOrderIndex()) + ", duration=" + String.valueOf(this.getDuration()) + ", type=" + String.valueOf(this.getType()) + ", objectives=" + this.getObjectives() + ", resources=" + this.getResources() + ", assignments=" + this.getAssignments() + ", quizzes=" + this.getQuizzes() + ", exercises=" + this.getExercises() + ", difficultyScore=" + String.valueOf(this.getDifficultyScore()) + ", prerequisites=" + this.getPrerequisites() + ", recommendedPreparation=" + this.getRecommendedPreparation() + ", averageCompletionTime=" + String.valueOf(this.getAverageCompletionTime()) + ", adaptivePath=" + this.getAdaptivePath() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ", updatedAt=" + String.valueOf(this.getUpdatedAt()) + ", module=" + String.valueOf(this.getModule()) + ")";
    }

    @Generated
    public Lesson() {
    }
}
