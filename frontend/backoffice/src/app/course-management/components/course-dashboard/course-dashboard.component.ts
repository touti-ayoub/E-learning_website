import { Component, OnInit } from "@angular/core";
import { CourseService } from "../../services/course.service";
import { IntelligentCourseService } from "../../services/intelligent-course.service";
import { Router } from "@angular/router";
import { Course } from "../../models/course.model";

@Component({
  selector: "app-course-dashboard",
  templateUrl: "./course-dashboard.component.html",
  styleUrls: ["./course-dashboard.component.scss"],
})
export class CourseDashboardComponent implements OnInit {
  totalCourses: number = 0;
  totalStudents: number = 0;
  averageCompletionRate: number = 0;
  averageEngagementScore: number = 0;
  coursesByCategory: any[] = [];
  recentCourses: any[] = [];
  topPerformingCourses: any[] = [];
  courseAnalytics: any = {};
  isLoading: boolean = true;
  course: any = {};
  selectedFile: File;

  constructor(
    private courseService: CourseService,
    private intelligentService: IntelligentCourseService,
    private router: Router
  ) {}

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.isLoading = true;
    Promise.all([
      this.loadOverviewStats(),
      this.loadCourseCategories(),
      this.loadRecentCourses(),
      this.loadTopPerformingCourses(),
      this.loadCourseAnalytics(),
    ]).finally(() => {
      this.isLoading = false;
    });
  }

  private async loadOverviewStats() {
    try {
      const stats = await this.courseService.getOverviewStats().toPromise();
      this.totalCourses = stats.totalCourses;
      this.totalStudents = stats.totalStudents;
      this.averageCompletionRate = stats.averageCompletionRate;
      this.averageEngagementScore = stats.averageEngagementScore;
    } catch (error) {
      console.error("Error loading overview stats:", error);
    }
  }

  private async loadCourseCategories() {
    try {
      this.coursesByCategory = await this.courseService
        .getCoursesByCategory()
        .toPromise();
    } catch (error) {
      console.error("Error loading course categories:", error);
    }
  }

  private async loadRecentCourses() {
    try {
      this.recentCourses = await this.courseService
        .getRecentCourses(5)
        .toPromise();
    } catch (error) {
      console.error("Error loading recent courses:", error);
    }
  }

  private async loadTopPerformingCourses() {
    try {
      this.topPerformingCourses = await this.courseService
        .getTopPerformingCourses(5)
        .toPromise();
    } catch (error) {
      console.error("Error loading top performing courses:", error);
    }
  }

  private async loadCourseAnalytics() {
    try {
      this.courseAnalytics = await this.intelligentService
        .getCourseAnalytics()
        .toPromise();
    } catch (error) {
      console.error("Error loading course analytics:", error);
    }
  }

  // Helper methods for the template
  getCompletionRateColor(rate: number): string {
    if (rate >= 0.8) return "success";
    if (rate >= 0.6) return "info";
    if (rate >= 0.4) return "warning";
    return "danger";
  }

  getEngagementScoreColor(score: number): string {
    if (score >= 4) return "success";
    if (score >= 3) return "info";
    if (score >= 2) return "warning";
    return "danger";
  }

  formatPercentage(value: number): string {
    return `${(value * 100).toFixed(1)}%`;
  }

  refreshData() {
    this.loadDashboardData();
  }

  onFileChange(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onSubmit() {
    const formData = new FormData();
    formData.append("title", this.course.title);
    formData.append("description", this.course.description);
    formData.append("coverImage", this.course.coverImage);
    formData.append("category", this.course.category);
    formData.append("status", this.course.status);
    formData.append("type", this.course.type);
    formData.append("price", this.course.price);
    formData.append("duration", this.course.duration);
    formData.append("language", this.course.language);
    formData.append("level", this.course.level);
    formData.append(
      "estimatedCompletionTime",
      this.course.estimatedCompletionTime
    );
    formData.append("targetAudience", this.course.targetAudience);
    formData.append("isAutomated", this.course.isAutomated);
    formData.append("difficultyScore", this.course.difficultyScore);
    formData.append("engagementScore", this.course.engagementScore);
    formData.append("completionRate", this.course.completionRate);
    formData.append("recommendationTags", this.course.recommendationTags);
    formData.append(
      "aiGeneratedTags",
      JSON.stringify(this.course.aiGeneratedTags)
    );
    formData.append("aiGeneratedSummary", this.course.aiGeneratedSummary);
    formData.append(
      "learningObjectives",
      JSON.stringify(this.course.learningObjectives)
    );
    formData.append("prerequisites", JSON.stringify(this.course.prerequisites));
    formData.append("skillWeights", JSON.stringify(this.course.skillWeights));

    if (this.course.isAutomated) {
      // Call the API for automated courses
      this.courseService
        .generateAutomatedCourse(formData)
        .subscribe((response) => {
          // Handle response
          this.router.navigate(["/courses"]);
        });
    } else {
      // Call the API for instructor-led courses
      const instructorId = this.course.instructorId; // Replace with actual instructor ID
      const courseData = this.prepareCourseData(); // Prepare course data as a Course object
      this.courseService
        .createCourse(courseData, instructorId)
        .subscribe((response) => {
          // Handle response
          this.router.navigate(["/courses"]);
        });
    }
  }

  private prepareCourseData(): Course {
    return {
      title: this.course.title,
      description: this.course.description,
      coverImage: this.course.coverImage,
      category: this.course.category,
      status: this.course.status,
      type: this.course.type,
      price: this.course.price,
      duration: this.course.duration,
      language: this.course.language,
      level: this.course.level,
      estimatedCompletionTime: this.course.estimatedCompletionTime,
      targetAudience: this.course.targetAudience,
      isAutomated: this.course.isAutomated,
      difficultyScore: this.course.difficultyScore,
      engagementScore: this.course.engagementScore,
      completionRate: this.course.completionRate,
      recommendationTags: this.course.recommendationTags,
      aiGeneratedTags: this.course.aiGeneratedTags,
      aiGeneratedSummary: this.course.aiGeneratedSummary,
      learningObjectives: this.course.learningObjectives,
      prerequisites: this.course.prerequisites,
      skillWeights: this.course.skillWeights,
    };
  }
}
