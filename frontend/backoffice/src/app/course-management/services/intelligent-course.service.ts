import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Course, LearningObjective } from '../models/course.model';

@Injectable({
  providedIn: 'root'
})
export class IntelligentCourseService {
  private apiUrl = `${environment.apiUrl}/intelligent`;

  constructor(private http: HttpClient) {}

  /**
   * Get AI-powered course analytics
   */
  getCourseAnalytics(): Observable<any> {
    return this.http.get(`${this.apiUrl}/analytics`);
  }

  /**
   * Get personalized course recommendations
   */
  getPersonalizedRecommendations(userId: number, limit: number = 5): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/recommendations/${userId}?limit=${limit}`);
  }

  /**
   * Generate adaptive learning path using AI
   */
  getAdaptiveLearningPath(courseId: number, userId: number): Observable<LearningObjective[]> {
    return this.http.get<LearningObjective[]>(`${this.apiUrl}/courses/${courseId}/learning-path/${userId}`);
  }

  /**
   * Identify knowledge gaps using AI analysis
   */
  getKnowledgeGaps(courseId: number, userId: number): Observable<Map<string, number>> {
    return this.http.get<Map<string, number>>(`${this.apiUrl}/courses/${courseId}/knowledge-gaps/${userId}`);
  }

  /**
   * Get AI-generated practice questions
   */
  getPersonalizedQuestions(courseId: number, userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/courses/${courseId}/practice-questions/${userId}`);
  }

  /**
   * Analyze student engagement patterns
   */
  getEngagementAnalysis(courseId: number, userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/courses/${courseId}/engagement/${userId}`);
  }

  /**
   * Get AI predictions for student performance
   */
  getPredictedPerformance(courseId: number, userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/courses/${courseId}/performance-prediction/${userId}`);
  }

  /**
   * Enrich course with AI-generated content
   */
  enrichCourseWithAI(courseId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/courses/${courseId}/enrich`, {});
  }

  /**
   * Generate automated course content
   */
  generateAutomatedContent(courseId: number, parameters: any): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/courses/${courseId}/generate-content`, parameters);
  }

  /**
   * Get AI-powered content recommendations
   */
  getContentRecommendations(courseId: number, userId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/courses/${courseId}/content-recommendations/${userId}`);
  }

  /**
   * Analyze course difficulty and suggest optimizations
   */
  analyzeCourseComplexity(courseId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/courses/${courseId}/complexity-analysis`);
  }

  /**
   * Generate personalized learning objectives
   */
  generatePersonalizedObjectives(courseId: number, userId: number): Observable<LearningObjective[]> {
    return this.http.get<LearningObjective[]>(`${this.apiUrl}/courses/${courseId}/personalized-objectives/${userId}`);
  }
} 