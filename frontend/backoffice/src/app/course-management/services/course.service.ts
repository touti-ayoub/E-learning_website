import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable, throwError } from "rxjs";
import { catchError } from "rxjs/operators";
import { environment } from "../../../environments/environment";
import {
  Course,
  CourseModule,
  Lesson,
  CourseProgress,
  LearningObjective,
  PrerequisiteSkill,
} from "../models/course.model";

@Injectable({
  providedIn: "root",
})
export class CourseService {
  private apiUrl = `${environment.apiUrl}/courses`;

  constructor(private http: HttpClient) {}

  /**
   * Get all courses
   */
  getAllCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(this.apiUrl);
  }

  /**
   * Get a specific course by ID
   */
  getCourseById(id: number): Observable<Course> {
    return this.http.get<Course>(`${this.apiUrl}/${id}`);
  }

  /**
   * Create a new course
   */
  createCourse(course: Course, instructorId: number): Observable<Course> {
    return this.http.post<Course>(
      `${this.apiUrl}/instructor/${instructorId}`,
      course
    );
  }

  /**
   * Update an existing course
   */
  updateCourse(id: number, course: Course): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}`, course);
  }

  /**
   * Delete a course
   */
  deleteCourse(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Publish a course
   */
  publishCourse(id: number): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}/publish`, {});
  }

  /**
   * Archive a course
   */
  archiveCourse(id: number): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${id}/archive`, {});
  }

  /**
   * Get modules for a course
   */
  getModules(courseId: number): Observable<CourseModule[]> {
    return this.http.get<CourseModule[]>(`${this.apiUrl}/${courseId}/modules`);
  }

  /**
   * Add a new module to a course
   */
  addModule(courseId: number, module: CourseModule): Observable<CourseModule> {
    return this.http.post<CourseModule>(
      `${this.apiUrl}/${courseId}/modules`,
      module
    );
  }

  /**
   * Update an existing module in a course
   */
  updateModule(
    courseId: number,
    moduleId: number,
    module: CourseModule
  ): Observable<CourseModule> {
    return this.http.put<CourseModule>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}`,
      module
    );
  }

  /**
   * Delete a module from a course
   */
  deleteModule(courseId: number, moduleId: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}`
    );
  }

  /**
   * Get lessons for a module in a course
   */
  getLessons(courseId: number, moduleId: number): Observable<Lesson[]> {
    return this.http.get<Lesson[]>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}/lessons`
    );
  }

  /**
   * Add a new lesson to a module in a course
   */
  addLesson(
    courseId: number,
    moduleId: number,
    lesson: Lesson
  ): Observable<Lesson> {
    return this.http.post<Lesson>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}/lessons`,
      lesson
    );
  }

  /**
   * Update an existing lesson in a module in a course
   */
  updateLesson(
    courseId: number,
    moduleId: number,
    lessonId: number,
    lesson: Lesson
  ): Observable<Lesson> {
    return this.http.put<Lesson>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}/lessons/${lessonId}`,
      lesson
    );
  }

  /**
   * Delete a lesson from a module in a course
   */
  deleteLesson(
    courseId: number,
    moduleId: number,
    lessonId: number
  ): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${courseId}/modules/${moduleId}/lessons/${lessonId}`
    );
  }

  /**
   * Enroll a user in a course
   */
  enrollUser(courseId: number, userId: number): Observable<void> {
    return this.http.post<void>(
      `${this.apiUrl}/${courseId}/enroll/${userId}`,
      {}
    );
  }

  /**
   * Get enrolled users for a course
   */
  getEnrolledUsers(courseId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${courseId}/enrolled-users`);
  }

  /**
   * Get user progress for a course
   */
  getUserProgress(
    courseId: number,
    userId: number
  ): Observable<CourseProgress> {
    return this.http.get<CourseProgress>(
      `${this.apiUrl}/${courseId}/progress/${userId}`
    );
  }

  /**
   * Update user progress for a course
   */
  updateUserProgress(
    courseId: number,
    userId: number,
    progress: CourseProgress
  ): Observable<CourseProgress> {
    return this.http.put<CourseProgress>(
      `${this.apiUrl}/${courseId}/progress/${userId}`,
      progress
    );
  }

  /**
   * Get learning objectives for a course
   */
  getLearningObjectives(courseId: number): Observable<LearningObjective[]> {
    return this.http.get<LearningObjective[]>(
      `${this.apiUrl}/${courseId}/objectives`
    );
  }

  /**
   * Update learning objectives for a course
   */
  updateLearningObjectives(
    courseId: number,
    objectives: LearningObjective[]
  ): Observable<LearningObjective[]> {
    return this.http.put<LearningObjective[]>(
      `${this.apiUrl}/${courseId}/objectives`,
      objectives
    );
  }

  /**
   * Get prerequisites for a course
   */
  getPrerequisites(courseId: number): Observable<PrerequisiteSkill[]> {
    return this.http.get<PrerequisiteSkill[]>(
      `${this.apiUrl}/${courseId}/prerequisites`
    );
  }

  /**
   * Update prerequisites for a course
   */
  updatePrerequisites(
    courseId: number,
    prerequisites: PrerequisiteSkill[]
  ): Observable<PrerequisiteSkill[]> {
    return this.http.put<PrerequisiteSkill[]>(
      `${this.apiUrl}/${courseId}/prerequisites`,
      prerequisites
    );
  }

  /**
   * Get course analytics
   */
  getCourseAnalytics(courseId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${courseId}/analytics`);
  }

  /**
   * Get recommended courses for a user
   */
  getRecommendedCourses(userId: number): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/recommended/${userId}`);
  }

  /**
   * Search courses
   */
  searchCourses(query: string): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/search?q=${query}`);
  }

  /**
   * Filter courses
   */
  filterCourses(filters: any): Observable<Course[]> {
    return this.http.post<Course[]>(`${this.apiUrl}/filter`, filters);
  }

  /**
   * Generate an automated course
   */
  generateAutomatedCourse(courseData: any): Observable<Course> {
    return this.http.post<Course>(`${this.apiUrl}/automated`, courseData).pipe(
      catchError((err) => {
        console.error("Error creating automated course:", err);
        return throwError(err);
      })
    );
  }

  /**
   * Update course metrics
   */
  updateCourseMetrics(courseId: number): Observable<Course> {
    return this.http.put<Course>(`${this.apiUrl}/${courseId}/metrics`, {});
  }

  getOverviewStats(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/stats/overview`);
  }

  getCoursesByCategory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/stats/by-category`);
  }

  getRecentCourses(limit: number): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/recent`, {
      params: { limit: limit.toString() },
    });
  }

  getTopPerformingCourses(limit: number): Observable<Course[]> {
    return this.http.get<Course[]>(`${this.apiUrl}/top-performing`, {
      params: { limit: limit.toString() },
    });
  }
}
