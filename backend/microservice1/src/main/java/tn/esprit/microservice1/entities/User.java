// Source code is decompiled from a .class file using FernFlower decompiler.
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
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Generated;

@Entity
public class User {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String profilePicture;
    private String bio;
    @Enumerated(EnumType.STRING)
    private UserRole role;
    private String preferredLanguage;
    private String learningStyle;
    @Column(
            columnDefinition = "TEXT"
    )
    private String interests;
    private String expertiseLevel;
    @Column(
            columnDefinition = "TEXT"
    )
    private String learningBehaviorPattern;
    private Double overallPerformance;
    @Column(
            columnDefinition = "TEXT"
    )
    private String recommendedTopics;
    @Column(
            columnDefinition = "TEXT"
    )
    private String personalizedLearningPath;
    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;
    @Column(
            name = "last_login"
    )
    private LocalDateTime lastLogin;
    @JsonIgnore
    @OneToMany(
            mappedBy = "instructor"
    )
    private Set<Course> createdCourses = new HashSet();
    @JsonIgnore
    @ManyToMany(
            mappedBy = "enrolledUsers"
    )
    private Set<Course> enrolledCourses = new HashSet();
    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.ALL}
    )
    private Set<CourseProgress> courseProgresses = new HashSet();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfilePicture() {
        return this.profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getBio() {
        return this.bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPreferredLanguage() {
        return this.preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getLearningStyle() {
        return this.learningStyle;
    }

    public void setLearningStyle(String learningStyle) {
        this.learningStyle = learningStyle;
    }

    public String getInterests() {
        return this.interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getExpertiseLevel() {
        return this.expertiseLevel;
    }

    public void setExpertiseLevel(String expertiseLevel) {
        this.expertiseLevel = expertiseLevel;
    }

    public String getLearningBehaviorPattern() {
        return this.learningBehaviorPattern;
    }

    public void setLearningBehaviorPattern(String learningBehaviorPattern) {
        this.learningBehaviorPattern = learningBehaviorPattern;
    }

    public Double getOverallPerformance() {
        return this.overallPerformance;
    }

    public void setOverallPerformance(Double overallPerformance) {
        this.overallPerformance = overallPerformance;
    }

    public String getRecommendedTopics() {
        return this.recommendedTopics;
    }

    public void setRecommendedTopics(String recommendedTopics) {
        this.recommendedTopics = recommendedTopics;
    }

    public String getPersonalizedLearningPath() {
        return this.personalizedLearningPath;
    }

    public void setPersonalizedLearningPath(String personalizedLearningPath) {
        this.personalizedLearningPath = personalizedLearningPath;
    }

    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastLogin() {
        return this.lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Set<Course> getCreatedCourses() {
        return this.createdCourses;
    }

    public Set<Course> getEnrolledCourses() {
        return this.enrolledCourses;
    }

    public Set<CourseProgress> getCourseProgresses() {
        return this.courseProgresses;
    }

    @JsonIgnore
    @Generated
    public void setCreatedCourses(final Set<Course> createdCourses) {
        this.createdCourses = createdCourses;
    }

    @JsonIgnore
    @Generated
    public void setEnrolledCourses(final Set<Course> enrolledCourses) {
        this.enrolledCourses = enrolledCourses;
    }

    @Generated
    public void setCourseProgresses(final Set<CourseProgress> courseProgresses) {
        this.courseProgresses = courseProgresses;
    }

    @Generated
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof User)) {
            return false;
        } else {
            User other = (User)o;
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

                Object this$overallPerformance = this.getOverallPerformance();
                Object other$overallPerformance = other.getOverallPerformance();
                if (this$overallPerformance == null) {
                    if (other$overallPerformance != null) {
                        return false;
                    }
                } else if (!this$overallPerformance.equals(other$overallPerformance)) {
                    return false;
                }

                label201: {
                    Object this$username = this.getUsername();
                    Object other$username = other.getUsername();
                    if (this$username == null) {
                        if (other$username == null) {
                            break label201;
                        }
                    } else if (this$username.equals(other$username)) {
                        break label201;
                    }

                    return false;
                }

                Object this$email = this.getEmail();
                Object other$email = other.getEmail();
                if (this$email == null) {
                    if (other$email != null) {
                        return false;
                    }
                } else if (!this$email.equals(other$email)) {
                    return false;
                }

                label187: {
                    Object this$fullName = this.getFullName();
                    Object other$fullName = other.getFullName();
                    if (this$fullName == null) {
                        if (other$fullName == null) {
                            break label187;
                        }
                    } else if (this$fullName.equals(other$fullName)) {
                        break label187;
                    }

                    return false;
                }

                Object this$profilePicture = this.getProfilePicture();
                Object other$profilePicture = other.getProfilePicture();
                if (this$profilePicture == null) {
                    if (other$profilePicture != null) {
                        return false;
                    }
                } else if (!this$profilePicture.equals(other$profilePicture)) {
                    return false;
                }

                label173: {
                    Object this$bio = this.getBio();
                    Object other$bio = other.getBio();
                    if (this$bio == null) {
                        if (other$bio == null) {
                            break label173;
                        }
                    } else if (this$bio.equals(other$bio)) {
                        break label173;
                    }

                    return false;
                }

                label166: {
                    Object this$role = this.getRole();
                    Object other$role = other.getRole();
                    if (this$role == null) {
                        if (other$role == null) {
                            break label166;
                        }
                    } else if (this$role.equals(other$role)) {
                        break label166;
                    }

                    return false;
                }

                Object this$preferredLanguage = this.getPreferredLanguage();
                Object other$preferredLanguage = other.getPreferredLanguage();
                if (this$preferredLanguage == null) {
                    if (other$preferredLanguage != null) {
                        return false;
                    }
                } else if (!this$preferredLanguage.equals(other$preferredLanguage)) {
                    return false;
                }

                label152: {
                    Object this$learningStyle = this.getLearningStyle();
                    Object other$learningStyle = other.getLearningStyle();
                    if (this$learningStyle == null) {
                        if (other$learningStyle == null) {
                            break label152;
                        }
                    } else if (this$learningStyle.equals(other$learningStyle)) {
                        break label152;
                    }

                    return false;
                }

                label145: {
                    Object this$interests = this.getInterests();
                    Object other$interests = other.getInterests();
                    if (this$interests == null) {
                        if (other$interests == null) {
                            break label145;
                        }
                    } else if (this$interests.equals(other$interests)) {
                        break label145;
                    }

                    return false;
                }

                Object this$expertiseLevel = this.getExpertiseLevel();
                Object other$expertiseLevel = other.getExpertiseLevel();
                if (this$expertiseLevel == null) {
                    if (other$expertiseLevel != null) {
                        return false;
                    }
                } else if (!this$expertiseLevel.equals(other$expertiseLevel)) {
                    return false;
                }

                Object this$learningBehaviorPattern = this.getLearningBehaviorPattern();
                Object other$learningBehaviorPattern = other.getLearningBehaviorPattern();
                if (this$learningBehaviorPattern == null) {
                    if (other$learningBehaviorPattern != null) {
                        return false;
                    }
                } else if (!this$learningBehaviorPattern.equals(other$learningBehaviorPattern)) {
                    return false;
                }

                label124: {
                    Object this$recommendedTopics = this.getRecommendedTopics();
                    Object other$recommendedTopics = other.getRecommendedTopics();
                    if (this$recommendedTopics == null) {
                        if (other$recommendedTopics == null) {
                            break label124;
                        }
                    } else if (this$recommendedTopics.equals(other$recommendedTopics)) {
                        break label124;
                    }

                    return false;
                }

                Object this$personalizedLearningPath = this.getPersonalizedLearningPath();
                Object other$personalizedLearningPath = other.getPersonalizedLearningPath();
                if (this$personalizedLearningPath == null) {
                    if (other$personalizedLearningPath != null) {
                        return false;
                    }
                } else if (!this$personalizedLearningPath.equals(other$personalizedLearningPath)) {
                    return false;
                }

                Object this$createdAt = this.getCreatedAt();
                Object other$createdAt = other.getCreatedAt();
                if (this$createdAt == null) {
                    if (other$createdAt != null) {
                        return false;
                    }
                } else if (!this$createdAt.equals(other$createdAt)) {
                    return false;
                }

                Object this$lastLogin = this.getLastLogin();
                Object other$lastLogin = other.getLastLogin();
                if (this$lastLogin == null) {
                    if (other$lastLogin != null) {
                        return false;
                    }
                } else if (!this$lastLogin.equals(other$lastLogin)) {
                    return false;
                }

                return true;
            }
        }
    }

    @Generated
    protected boolean canEqual(final Object other) {
        return other instanceof User;
    }

    @Generated
    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $overallPerformance = this.getOverallPerformance();
        result = result * 59 + ($overallPerformance == null ? 43 : $overallPerformance.hashCode());
        Object $username = this.getUsername();
        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        Object $fullName = this.getFullName();
        result = result * 59 + ($fullName == null ? 43 : $fullName.hashCode());
        Object $profilePicture = this.getProfilePicture();
        result = result * 59 + ($profilePicture == null ? 43 : $profilePicture.hashCode());
        Object $bio = this.getBio();
        result = result * 59 + ($bio == null ? 43 : $bio.hashCode());
        Object $role = this.getRole();
        result = result * 59 + ($role == null ? 43 : $role.hashCode());
        Object $preferredLanguage = this.getPreferredLanguage();
        result = result * 59 + ($preferredLanguage == null ? 43 : $preferredLanguage.hashCode());
        Object $learningStyle = this.getLearningStyle();
        result = result * 59 + ($learningStyle == null ? 43 : $learningStyle.hashCode());
        Object $interests = this.getInterests();
        result = result * 59 + ($interests == null ? 43 : $interests.hashCode());
        Object $expertiseLevel = this.getExpertiseLevel();
        result = result * 59 + ($expertiseLevel == null ? 43 : $expertiseLevel.hashCode());
        Object $learningBehaviorPattern = this.getLearningBehaviorPattern();
        result = result * 59 + ($learningBehaviorPattern == null ? 43 : $learningBehaviorPattern.hashCode());
        Object $recommendedTopics = this.getRecommendedTopics();
        result = result * 59 + ($recommendedTopics == null ? 43 : $recommendedTopics.hashCode());
        Object $personalizedLearningPath = this.getPersonalizedLearningPath();
        result = result * 59 + ($personalizedLearningPath == null ? 43 : $personalizedLearningPath.hashCode());
        Object $createdAt = this.getCreatedAt();
        result = result * 59 + ($createdAt == null ? 43 : $createdAt.hashCode());
        Object $lastLogin = this.getLastLogin();
        result = result * 59 + ($lastLogin == null ? 43 : $lastLogin.hashCode());
        return result;
    }

    @Generated
    public String toString() {
        String var10000 = String.valueOf(this.getId());
        return "User(id=" + var10000 + ", username=" + this.getUsername() + ", email=" + this.getEmail() + ", fullName=" + this.getFullName() + ", profilePicture=" + this.getProfilePicture() + ", bio=" + this.getBio() + ", role=" + String.valueOf(this.getRole()) + ", preferredLanguage=" + this.getPreferredLanguage() + ", learningStyle=" + this.getLearningStyle() + ", interests=" + this.getInterests() + ", expertiseLevel=" + this.getExpertiseLevel() + ", learningBehaviorPattern=" + this.getLearningBehaviorPattern() + ", overallPerformance=" + String.valueOf(this.getOverallPerformance()) + ", recommendedTopics=" + this.getRecommendedTopics() + ", personalizedLearningPath=" + this.getPersonalizedLearningPath() + ", createdAt=" + String.valueOf(this.getCreatedAt()) + ", lastLogin=" + String.valueOf(this.getLastLogin()) + ")";
    }

    @Generated
    public User() {
    }
}
