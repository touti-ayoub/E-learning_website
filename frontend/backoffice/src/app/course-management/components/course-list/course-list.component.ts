import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { Router } from "@angular/router";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { MatSort } from "@angular/material/sort";
import { CourseService } from "../../services/course.service";
import { NotificationService } from "../../../shared/services/notification.service";
import { Course } from "../../models/course.model";

@Component({
  selector: "app-course-list",
  templateUrl: "./course-list.component.html",
  styleUrls: ["./course-list.component.scss"],
})
export class CourseListComponent implements OnInit, AfterViewInit {
  courses: Course[] = [];
  filteredCourses: Course[] = [];
  searchQuery: string = "";
  dataSource: MatTableDataSource<Course>;

  @ViewChild(MatPaginator) paginator: MatPaginator;
  @ViewChild(MatSort) sort: MatSort;

  // Pagination
  currentPage: number = 1;
  pageSize: number = 10;
  totalItems: number = 0;
  Math = Math; // Make Math available to template

  constructor(
    private courseService: CourseService,
    private router: Router,
    private notificationService: NotificationService
  ) {
    this.dataSource = new MatTableDataSource<Course>();
  }

  ngOnInit(): void {
    this.loadCourses();
  }

  ngAfterViewInit() {
    if (this.dataSource) {
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    }
  }

  loadCourses(): void {
    this.courseService.getAllCourses().subscribe({
      next: (courses) => {
        this.courses = courses;
        this.dataSource.data = courses;
        this.totalItems = courses.length;
        this.filterCourses();
      },
      error: (error) => {
        console.error("Error loading courses:", error);
        this.notificationService.showNotification(
          "Error loading courses",
          "danger"
        );
      },
    });
  }

  filterCourses(): void {
    if (!this.searchQuery) {
      this.filteredCourses = this.courses;
    } else {
      const query = this.searchQuery.toLowerCase();
      this.filteredCourses = this.courses.filter(
        (course) =>
          course.title.toLowerCase().includes(query) ||
          course.description.toLowerCase().includes(query) ||
          course.category.toLowerCase().includes(query)
      );
    }
    this.dataSource.data = this.filteredCourses;
    if (this.paginator) {
      this.paginator.firstPage();
    }
  }

  applyFilter(event: Event): void {
    this.searchQuery = (event.target as HTMLInputElement).value;
    this.filterCourses();
  }

  getPaginatedCourses(): Course[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredCourses.slice(startIndex, startIndex + this.pageSize);
  }

  onPageChange(page: number): void {
    this.currentPage = page;
  }

  previousPage(): void {
    if (this.paginator && this.paginator.hasPreviousPage()) {
      this.paginator.previousPage();
      this.currentPage = this.paginator.pageIndex + 1;
    }
  }

  nextPage(): void {
    if (this.paginator && this.hasNextPage()) {
      this.paginator.nextPage();
      this.currentPage = this.paginator.pageIndex + 1;
    }
  }

  hasNextPage(): boolean {
    if (!this.paginator) return false;
    return this.paginator.hasNextPage();
  }

  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case "published":
        return "badge-success";
      case "draft":
        return "badge-warning";
      case "archived":
        return "badge-danger";
      default:
        return "badge-info";
    }
  }

  getProgressBarClass(completion: number): string {
    if (completion >= 0.7) return "progress-bar-success";
    if (completion >= 0.4) return "progress-bar-warning";
    return "progress-bar-danger";
  }

  editCourse(courseId: string): void {
    this.router.navigate(["/courses/edit", courseId]);
  }

  viewCourse(courseId: string): void {
    this.router.navigate(["/courses", courseId]);
  }

  deleteCourse(courseId: string): void {
    if (confirm("Are you sure you want to delete this course?")) {
      this.courseService.deleteCourse(Number(courseId)).subscribe({
        next: () => {
          this.notificationService.showNotification(
            "Course deleted successfully",
            "success"
          );
          this.loadCourses();
        },
        error: (error) => {
          console.error("Error deleting course:", error);
          this.notificationService.showNotification(
            "Error deleting course",
            "danger"
          );
        },
      });
    }
  }

  createNewCourse(): void {
    this.router.navigate(["/courses/new"]);
  }
}
