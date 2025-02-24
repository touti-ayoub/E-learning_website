package tn.esprit.microservice1.entities;

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
public class CourseProgress {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    @ManyToOne
    @JoinColumn(
            name = "user_id"
    )
    private User user;
    @ManyToOne
    @JoinColumn(
            name = "course_id"
    )
    private Course course;
    private Double progressPercentage;
    private String currentModuleId;
    private String currentLessonId;
    private LocalDateTime lastAccessDate;
    private Integer timeSpentMinutes;
    private Double engagementScore;
    private Double performanceScore;
    private String learningStyle;
    private String strengthAreas;
    private String weaknessAreas;
    private String personalizedRecommendations;
    @Enumerated(EnumType.STRING)
    private ProgressStatus status;
    @Column(
            name = "started_at"
    )
    private LocalDateTime startedAt;
    @Column(
            name = "completed_at"
    )
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        this.startedAt = LocalDateTime.now();
        this.lastAccessDate = LocalDateTime.now();
        this.status = ProgressStatus.IN_PROGRESS;
        this.progressPercentage = 0.0;
        this.timeSpentMinutes = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastAccessDate = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Double getProgressPercentage() {
        return this.progressPercentage;
    }

    public void setProgressPercentage(Double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public String getCurrentModuleId() {
        return this.currentModuleId;
    }

    public void setCurrentModuleId(String currentModuleId) {
        this.currentModuleId = currentModuleId;
    }

    public String getCurrentLessonId() {
        return this.currentLessonId;
    }

    public void setCurrentLessonId(String currentLessonId) {
        this.currentLessonId = currentLessonId;
    }

    public LocalDateTime getLastAccessDate() {
        return this.lastAccessDate;
    }

    public void setLastAccessDate(LocalDateTime lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public Integer getTimeSpentMinutes() {
        return this.timeSpentMinutes;
    }

    public void setTimeSpentMinutes(Integer timeSpentMinutes) {
        this.timeSpentMinutes = timeSpentMinutes;
    }

    public Double getEngagementScore() {
        return this.engagementScore;
    }

    public void setEngagementScore(Double engagementScore) {
        this.engagementScore = engagementScore;
    }

    public Double getPerformanceScore() {
        return this.performanceScore;
    }

    public void setPerformanceScore(Double performanceScore) {
        this.performanceScore = performanceScore;
    }

    public String getLearningStyle() {
        return this.learningStyle;
    }

    public void setLearningStyle(String learningStyle) {
        this.learningStyle = learningStyle;
    }

    public String getStrengthAreas() {
        return this.strengthAreas;
    }

    public void setStrengthAreas(String strengthAreas) {
        this.strengthAreas = strengthAreas;
    }

    public String getWeaknessAreas() {
        return this.weaknessAreas;
    }

    public void setWeaknessAreas(String weaknessAreas) {
        this.weaknessAreas = weaknessAreas;
    }

    public String getPersonalizedRecommendations() {
        return this.personalizedRecommendations;
    }

    public void setPersonalizedRecommendations(String personalizedRecommendations) {
        this.personalizedRecommendations = personalizedRecommendations;
    }

    public ProgressStatus getStatus() {
        return this.status;
    }

    public void setStatus(ProgressStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return this.completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof CourseProgress)) {
            return false;
        } else {
            CourseProgress other = (CourseProgress)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label215: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label215;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label215;
                    }

                    return false;
                }

                Object this$progressPercentage = this.getProgressPercentage();
                Object other$progressPercentage = other.getProgressPercentage();
                if (this$progressPercentage == null) {
                    if (other$progressPercentage != null) {
                        return false;
                    }
                } else if (!this$progressPercentage.equals(other$progressPercentage)) {
                    return false;
                }

                label201: {
                    Object this$timeSpentMinutes = this.getTimeSpentMinutes();
                    Object other$timeSpentMinutes = other.getTimeSpentMinutes();
                    if (this$timeSpentMinutes == null) {
                        if (other$timeSpentMinutes == null) {
                            break label201;
                        }
                    } else if (this$timeSpentMinutes.equals(other$timeSpentMinutes)) {
                        break label201;
                    }

                    return false;
                }

                Object this$engagementScore = this.getEngagementScore();
                Object other$engagementScore = other.getEngagementScore();
                if (this$engagementScore == null) {
                    if (other$engagementScore != null) {
                        return false;
                    }
                } else if (!this$engagementScore.equals(other$engagementScore)) {
                    return false;
                }

                label187: {
                    Object this$performanceScore = this.getPerformanceScore();
                    Object other$performanceScore = other.getPerformanceScore();
                    if (this$performanceScore == null) {
                        if (other$performanceScore == null) {
                            break label187;
                        }
                    } else if (this$performanceScore.equals(other$performanceScore)) {
                        break label187;
                    }

                    return false;
                }

                Object this$user = this.getUser();
                Object other$user = other.getUser();
                if (this$user == null) {
                    if (other$user != null) {
                        return false;
                    }
                } else if (!this$user.equals(other$user)) {
                    return false;
                }

                label173: {
                    Object this$course = this.getCourse();
                    Object other$course = other.getCourse();
                    if (this$course == null) {
                        if (other$course == null) {
                            break label173;
                        }
                    } else if (this$course.equals(other$course)) {
                        break label173;
                    }

                    return false;
                }

                label166: {
                    Object this$currentModuleId = this.getCurrentModuleId();
                    Object other$currentModuleId = other.getCurrentModuleId();
                    if (this$currentModuleId == null) {
                        if (other$currentModuleId == null) {
                            break label166;
                        }
                    } else if (this$currentModuleId.equals(other$currentModuleId)) {
                        break label166;
                    }

                    return false;
                }

                Object this$currentLessonId = this.getCurrentLessonId();
                Object other$currentLessonId = other.getCurrentLessonId();
                if (this$currentLessonId == null) {
                    if (other$currentLessonId != null) {
                        return false;
                    }
                } else if (!this$currentLessonId.equals(other$currentLessonId)) {
                    return false;
                }

                label152: {
                    Object this$lastAccessDate = this.getLastAccessDate();
                    Object other$lastAccessDate = other.getLastAccessDate();
                    if (this$lastAccessDate == null) {
                        if (other$lastAccessDate == null) {
                            break label152;
                        }
                    } else if (this$lastAccessDate.equals(other$lastAccessDate)) {
                        break label152;
                    }

                    return false;
                }

                label145: {
                    Object this$learningStyle = this.getLearningStyle();
                    Object other$learningStyle = other.getLearningStyle();
                    if (this$learningStyle == null) {
                        if (other$learningStyle == null) {
                            break label145;
                        }
                    } else if (this$learningStyle.equals(other$learningStyle)) {
                        break label145;
                    }

                    return false;
                }

                Object this$strengthAreas = this.getStrengthAreas();
                Object other$strengthAreas = other.getStrengthAreas();
                if (this$strengthAreas == null) {
                    if (other$strengthAreas != null) {
                        return false;
                    }
                } else if (!this$strengthAreas.equals(other$strengthAreas)) {
                    return false;
                }

                Object this$weaknessAreas = this.getWeaknessAreas();
                Object other$weaknessAreas = other.getWeaknessAreas();
                if (this$weaknessAreas == null) {
                    if (other$weaknessAreas != null) {
                        return false;
                    }
                } else if (!this$weaknessAreas.equals(other$weaknessAreas)) {
                    return false;
                }

                label124: {
                    Object this$personalizedRecommendations = this.getPersonalizedRecommendations();
                    Object other$personalizedRecommendations = other.getPersonalizedRecommendations();
                    if (this$personalizedRecommendations == null) {
                        if (other$personalizedRecommendations == null) {
                            break label124;
                        }
                    } else if (this$personalizedRecommendations.equals(other$personalizedRecommendations)) {
                        break label124;
                    }

                    return false;
                }

                Object this$status = this.getStatus();
                Object other$status = other.getStatus();
                if (this$status == null) {
                    if (other$status != null) {
                        return false;
                    }
                } else if (!this$status.equals(other$status)) {
                    return false;
                }

                Object this$startedAt = this.getStartedAt();
                Object other$startedAt = other.getStartedAt();
                if (this$startedAt == null) {
                    if (other$startedAt != null) {
                        return false;
                    }
                } else if (!this$startedAt.equals(other$startedAt)) {
                    return false;
                }

                Object this$completedAt = this.getCompletedAt();
                Object other$completedAt = other.getCompletedAt();
                if (this$completedAt == null) {
                    if (other$completedAt != null) {
                        return false;
                    }
                } else if (!this$completedAt.equals(other$completedAt)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof CourseProgress;
    }

    @Generated
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $progressPercentage = this.getProgressPercentage();
        result = result * 59 + ($progressPercentage == null ? 43 : $progressPercentage.hashCode());
        Object $timeSpentMinutes = this.getTimeSpentMinutes();
        result = result * 59 + ($timeSpentMinutes == null ? 43 : $timeSpentMinutes.hashCode());
        Object $engagementScore = this.getEngagementScore();
        result = result * 59 + ($engagementScore == null ? 43 : $engagementScore.hashCode());
        Object $performanceScore = this.getPerformanceScore();
        result = result * 59 + ($performanceScore == null ? 43 : $performanceScore.hashCode());
        Object $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        Object $course = this.getCourse();
        result = result * 59 + ($course == null ? 43 : $course.hashCode());
        Object $currentModuleId = this.getCurrentModuleId();
        result = result * 59 + ($currentModuleId == null ? 43 : $currentModuleId.hashCode());
        Object $currentLessonId = this.getCurrentLessonId();
        result = result * 59 + ($currentLessonId == null ? 43 : $currentLessonId.hashCode());
        Object $lastAccessDate = this.getLastAccessDate();
        result = result * 59 + ($lastAccessDate == null ? 43 : $lastAccessDate.hashCode());
        Object $learningStyle = this.getLearningStyle();
        result = result * 59 + ($learningStyle == null ? 43 : $learningStyle.hashCode());
        Object $strengthAreas = this.getStrengthAreas();
        result = result * 59 + ($strengthAreas == null ? 43 : $strengthAreas.hashCode());
        Object $weaknessAreas = this.getWeaknessAreas();
        result = result * 59 + ($weaknessAreas == null ? 43 : $weaknessAreas.hashCode());
        Object $personalizedRecommendations = this.getPersonalizedRecommendations();
        result = result * 59 + ($personalizedRecommendations == null ? 43 : $personalizedRecommendations.hashCode());
        Object $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : $status.hashCode());
        Object $startedAt = this.getStartedAt();
        result = result * 59 + ($startedAt == null ? 43 : $startedAt.hashCode());
        Object $completedAt = this.getCompletedAt();
        result = result * 59 + ($completedAt == null ? 43 : $completedAt.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = String.valueOf(this.getId());
        return "CourseProgress(id=" + var10000 + ", user=" + String.valueOf(this.getUser()) + ", course=" + String.valueOf(this.getCourse()) + ", progressPercentage=" + String.valueOf(this.getProgressPercentage()) + ", currentModuleId=" + this.getCurrentModuleId() + ", currentLessonId=" + this.getCurrentLessonId() + ", lastAccessDate=" + String.valueOf(this.getLastAccessDate()) + ", timeSpentMinutes=" + String.valueOf(this.getTimeSpentMinutes()) + ", engagementScore=" + String.valueOf(this.getEngagementScore()) + ", performanceScore=" + String.valueOf(this.getPerformanceScore()) + ", learningStyle=" + this.getLearningStyle() + ", strengthAreas=" + this.getStrengthAreas() + ", weaknessAreas=" + this.getWeaknessAreas() + ", personalizedRecommendations=" + this.getPersonalizedRecommendations() + ", status=" + String.valueOf(this.getStatus()) + ", startedAt=" + String.valueOf(this.getStartedAt()) + ", completedAt=" + String.valueOf(this.getCompletedAt()) + ")";
    }

    @Generated
    public CourseProgress() {
    }
}
