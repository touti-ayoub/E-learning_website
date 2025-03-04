package tn.esprit.microservice1.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import jakarta.persistence.Table;
import jakarta.persistence.*;

@Entity
@Table(name = "course_module")
public class CourseModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;

    private Integer duration;

    @Column(name = "completion_rate")
    private Double completionRate;

    @Column(name = "difficulty_level")
    private Double difficultyLevel;

    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;

    @Column(columnDefinition = "TEXT")
    private String prerequisites;

    @Column(name = "recommended_path", columnDefinition = "TEXT")
    private String recommendedPath;

    @Enumerated(EnumType.STRING)
    private ModuleType type;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons = new ArrayList<>();

    @ManyToMany(mappedBy = "completedModules")
    private Set<CourseProgress> completedByStudents = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
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
    public Integer getOrderIndex() {
        return this.orderIndex;
    }

    @Generated
    public Integer getDuration() {
        return this.duration;
    }

    @Generated
    public String getLearningObjectives() {
        return this.learningObjectives;
    }

    @Generated
    public ModuleType getType() {
        return this.type;
    }

    @Generated
    public Double getDifficultyLevel() {
        return this.difficultyLevel;
    }

    @Generated
    public String getPrerequisites() {
        return this.prerequisites;
    }

    @Generated
    public String getRecommendedPath() {
        return this.recommendedPath;
    }

    @Generated
    public Double getCompletionRate() {
        return this.completionRate;
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
    public Course getCourse() {
        return this.course;
    }

    @Generated
    public List<Lesson> getLessons() {
        return this.lessons;
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
    public void setOrderIndex(final Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Generated
    public void setDuration(final Integer duration) {
        this.duration = duration;
    }

    @Generated
    public void setLearningObjectives(final String learningObjectives) {
        this.learningObjectives = learningObjectives;
    }

    @Generated
    public void setType(final ModuleType type) {
        this.type = type;
    }

    @Generated
    public void setDifficultyLevel(final Double difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    @Generated
    public void setPrerequisites(final String prerequisites) {
        this.prerequisites = prerequisites;
    }

    @Generated
    public void setRecommendedPath(final String recommendedPath) {
        this.recommendedPath = recommendedPath;
    }

    @Generated
    public void setCompletionRate(final Double completionRate) {
        this.completionRate = completionRate;
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
    public void setCourse(final Course course) {
        this.course = course;
    }

    @Generated
    public void setLessons(final List<Lesson> lessons) {
        this.lessons = lessons;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CourseModule)) {
            return false;
        } else {
            CourseModule other = (CourseModule)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                Object this$id = this.getId();
                Object other$id = other.getId();
                if (this$id == null) {
                    if (other$id != null) {
                        return false;
                    }
                } else if (!this$id.equals(other$id)) {
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

                Object this$duration = this.getDuration();
                Object other$duration = other.getDuration();
                if (this$duration == null) {
                    if (other$duration != null) {
                        return false;
                    }
                } else if (!this$duration.equals(other$duration)) {
                    return false;
                }

                label158: {
                    Object this$difficultyLevel = this.getDifficultyLevel();
                    Object other$difficultyLevel = other.getDifficultyLevel();
                    if (this$difficultyLevel == null) {
                        if (other$difficultyLevel == null) {
                            break label158;
                        }
                    } else if (this$difficultyLevel.equals(other$difficultyLevel)) {
                        break label158;
                    }

                    return false;
                }

                label151: {
                    Object this$completionRate = this.getCompletionRate();
                    Object other$completionRate = other.getCompletionRate();
                    if (this$completionRate == null) {
                        if (other$completionRate == null) {
                            break label151;
                        }
                    } else if (this$completionRate.equals(other$completionRate)) {
                        break label151;
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

                label137: {
                    Object this$description = this.getDescription();
                    Object other$description = other.getDescription();
                    if (this$description == null) {
                        if (other$description == null) {
                            break label137;
                        }
                    } else if (this$description.equals(other$description)) {
                        break label137;
                    }

                    return false;
                }

                label130: {
                    Object this$learningObjectives = this.getLearningObjectives();
                    Object other$learningObjectives = other.getLearningObjectives();
                    if (this$learningObjectives == null) {
                        if (other$learningObjectives == null) {
                            break label130;
                        }
                    } else if (this$learningObjectives.equals(other$learningObjectives)) {
                        break label130;
                    }

                    return false;
                }

                Object this$type = this.getType();
                Object other$type = other.getType();
                if (this$type == null) {
                    if (other$type != null) {
                        return false;
                    }
                } else if (!this$type.equals(other$type)) {
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

                label109: {
                    Object this$recommendedPath = this.getRecommendedPath();
                    Object other$recommendedPath = other.getRecommendedPath();
                    if (this$recommendedPath == null) {
                        if (other$recommendedPath == null) {
                            break label109;
                        }
                    } else if (this$recommendedPath.equals(other$recommendedPath)) {
                        break label109;
                    }

                    return false;
                }

                label102: {
                    Object this$createdAt = this.getCreatedAt();
                    Object other$createdAt = other.getCreatedAt();
                    if (this$createdAt == null) {
                        if (other$createdAt == null) {
                            break label102;
                        }
                    } else if (this$createdAt.equals(other$createdAt)) {
                        break label102;
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

                Object this$course = this.getCourse();
                Object other$course = other.getCourse();
                if (this$course == null) {
                    if (other$course != null) {
                        return false;
                    }
                } else if (!this$course.equals(other$course)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof CourseModule;
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
        Object $difficultyLevel = this.getDifficultyLevel();
        result = result * 59 + ($difficultyLevel == null ? 43 : $difficultyLevel.hashCode());
        Object $completionRate = this.getCompletionRate();
        result = result * 59 + ($completionRate == null ? 43 : $completionRate.hashCode());
        Object $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $learningObjectives = this.getLearningObjectives();
        result = result * 59 + ($learningObjectives == null ? 43 : $learningObjectives.hashCode());
        Object $type = this.getType();
        result = result * 59 + ($type == null ? 43 : $type.hashCode());
        Object $prerequisites = this.getPrerequisites();
        result = result * 59 + ($prerequisites == null ? 43 : $prerequisites.hashCode());
        Object $recommendedPath = this.getRecommendedPath();
        result = result * 59 + ($recommendedPath == null ? 43 : $recommendedPath.hashCode());
        Object $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        Object $updatedAt = this.getUpdatedAt();
        result = result * 59 + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        Object $course = this.getCourse();
        result = result * 59 + ($course == null ? 43 : $course.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = String.valueOf(this.getId());
        return "CourseModule(id=" + var10000 + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", orderIndex=" + String.valueOf(this.getOrderIndex()) + ", duration=" + String.valueOf(this.getDuration()) + ", learningObjectives=" + this.getLearningObjectives() + ", type=" + String.valueOf(this.getType()) + ", difficultyLevel=" + String.valueOf(this.getDifficultyLevel()) + ", prerequisites=" + this.getPrerequisites() + ", recommendedPath=" + this.getRecommendedPath() + ", completionRate=" + String.valueOf(this.getCompletionRate()) + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ", updatedAt=" + String.valueOf(this.getUpdatedAt()) + ", course=" + String.valueOf(this.getCourse()) + ")";
    }

    @Generated
    public CourseModule() {
    }
}
