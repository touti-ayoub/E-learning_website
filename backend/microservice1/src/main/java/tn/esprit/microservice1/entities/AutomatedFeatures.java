package tn.esprit.microservice1.entities;

import jakarta.persistence.Embeddable;

@Embeddable
public class AutomatedFeatures {
    private boolean hasAutoGrading;
    private boolean hasPeerReview;
    private boolean hasAdaptiveLearning;
    private boolean automatedFeedback;
    private String progressionRules;
    private String completionCriteria;

    public AutomatedFeatures() {
    }

    public boolean isHasAutoGrading() {
        return this.hasAutoGrading;
    }

    public void setHasAutoGrading(boolean hasAutoGrading) {
        this.hasAutoGrading = hasAutoGrading;
    }

    public boolean isHasPeerReview() {
        return this.hasPeerReview;
    }

    public void setHasPeerReview(boolean hasPeerReview) {
        this.hasPeerReview = hasPeerReview;
    }

    public boolean isHasAdaptiveLearning() {
        return this.hasAdaptiveLearning;
    }

    public void setHasAdaptiveLearning(boolean hasAdaptiveLearning) {
        this.hasAdaptiveLearning = hasAdaptiveLearning;
    }

    public boolean isAutomatedFeedback() {
        return this.automatedFeedback;
    }

    public void setAutomatedFeedback(boolean automatedFeedback) {
        this.automatedFeedback = automatedFeedback;
    }

    public String getProgressionRules() {
        return this.progressionRules;
    }

    public void setProgressionRules(String progressionRules) {
        this.progressionRules = progressionRules;
    }

    public String getCompletionCriteria() {
        return this.completionCriteria;
    }

    public void setCompletionCriteria(String completionCriteria) {
        this.completionCriteria = completionCriteria;
    }
}
