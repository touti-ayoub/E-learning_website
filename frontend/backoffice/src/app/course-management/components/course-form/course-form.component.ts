import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, FormArray, Validators } from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { CourseService } from "../../services/course.service";
import { NotificationService } from "../../../shared/services/notification.service";
import {
  Course,
  CourseType,
  CourseStatus,
  ModuleType,
  LessonType,
} from "../../models/course.model";

@Component({
  selector: "app-course-form",
  templateUrl: "./course-form.component.html",
  styleUrls: ["./course-form.component.scss"],
})
export class CourseFormComponent implements OnInit {
  courseForm: FormGroup;
  isEditMode = false;
  isLoading = false;
  courseId: number | null = null;
  selectedFile: File;

  courseTypes = Object.values(CourseType);
  courseStatuses = Object.values(CourseStatus);
  moduleTypes = Object.values(ModuleType);
  lessonTypes = Object.values(LessonType);

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService
  ) {
    this.initForm();
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get("id");
    if (id) {
      this.courseId = +id;
      this.isEditMode = true;
      this.loadCourse(this.courseId);
    }
  }

  private initForm() {
    this.courseForm = this.fb.group({
      title: ["", [Validators.required, Validators.minLength(3)]],
      description: ["", [Validators.required, Validators.minLength(10)]],
      coverImage: [""],
      category: ["", Validators.required],
      status: [CourseStatus.DRAFT],
      type: [CourseType.STATIC],
      price: [0, [Validators.required, Validators.min(0)]],
      duration: [null],
      language: ["", Validators.required],
      level: ["", Validators.required],
      estimatedCompletionTime: [null],
      targetAudience: [""],
      isAutomated: [false],
      difficultyScore: [null, [Validators.min(0)]],
      engagementScore: [null, [Validators.min(0)]],
      completionRate: [null, [Validators.min(0), Validators.max(100)]],
      recommendationTags: [""],
      aiGeneratedTags: [""],
      aiGeneratedSummary: [""],
      learningObjectives: this.fb.array([]),
      prerequisites: this.fb.array([]),
      skillWeights: this.fb.array([]),
      modules: this.fb.array([]),
    });
  }

  private loadCourse(id: number) {
    this.isLoading = true;
    this.courseService.getCourseById(id).subscribe({
      next: (course) => {
        this.patchFormWithCourse(course);
        this.isLoading = false;
      },
      error: (error) => {
        console.error("Error loading course:", error);
        this.notificationService.showNotification(
          "Error loading course",
          "danger"
        );
        this.router.navigate(["/courses"]);
        this.isLoading = false;
      },
    });
  }

  private patchFormWithCourse(course: Course) {
    this.courseForm.patchValue({
      title: course.title,
      description: course.description,
      coverImage: course.coverImage,
      category: course.category,
      status: course.status,
      type: course.type,
      price: course.price,
      duration: course.duration,
      language: course.language,
      level: course.level,
      estimatedCompletionTime: course.estimatedCompletionTime,
      targetAudience: course.targetAudience,
      isAutomated: course.isAutomated,
    });

    // Clear and rebuild form arrays
    this.clearFormArrays();

    // Patch learning objectives
    if (course.learningObjectives) {
      course.learningObjectives.forEach((objective) => {
        this.addLearningObjective(objective);
      });
    }

    // Patch prerequisites
    if (course.prerequisites) {
      course.prerequisites.forEach((prerequisite) => {
        this.addPrerequisite(prerequisite);
      });
    }

    // Patch skill weights
    if (course.skillWeights) {
      Object.entries(course.skillWeights).forEach(([skill, weight]) => {
        this.addSkillWeight(skill, weight);
      });
    }

    // Patch modules
    if (course.modules) {
      course.modules.forEach((module) => {
        this.addModule(module);
      });
    }
  }

  private clearFormArrays() {
    const arrays = [
      "learningObjectives",
      "prerequisites",
      "skillWeights",
      "modules",
    ];
    arrays.forEach((arrayName) => {
      while (this.getFormArray(arrayName).length) {
        this.getFormArray(arrayName).removeAt(0);
      }
    });
  }

  getFormArray(name: string): FormArray {
    return this.courseForm.get(name) as FormArray;
  }

  addLearningObjective(objective: any = {}) {
    const objectiveGroup = this.fb.group({
      objective: [objective.objective || "", Validators.required],
      description: [objective.description || ""],
      difficultyLevel: [objective.difficultyLevel || null],
      completionThreshold: [objective.completionThreshold || null],
      isAiGenerated: [objective.isAiGenerated || false],
    });
    this.getFormArray("learningObjectives").push(objectiveGroup);
  }

  addPrerequisite(prerequisite: any = {}) {
    const prerequisiteGroup = this.fb.group({
      skillName: [prerequisite.skillName || "", Validators.required],
      description: [prerequisite.description || ""],
      proficiencyLevel: [prerequisite.proficiencyLevel || null],
      isRequired: [prerequisite.isRequired || false],
      weightInCourse: [prerequisite.weightInCourse || null],
    });
    this.getFormArray("prerequisites").push(prerequisiteGroup);
  }

  addSkillWeight(skill: string = "", weight: number = 0) {
    const skillWeightGroup = this.fb.group({
      skill: [skill, Validators.required],
      weight: [
        weight,
        [Validators.required, Validators.min(0), Validators.max(1)],
      ],
    });
    this.getFormArray("skillWeights").push(skillWeightGroup);
  }

  addModule(module: any = {}) {
    const moduleGroup = this.fb.group({
      title: [module.title || "", Validators.required],
      description: [module.description || ""],
      orderIndex: [module.orderIndex || this.getFormArray("modules").length],
      duration: [module.duration || null],
      type: [module.type || ModuleType.THEORY],
      learningObjectives: [module.learningObjectives || ""],
      prerequisites: [module.prerequisites || ""],
      lessons: this.fb.array([]),
    });

    if (module.lessons) {
      module.lessons.forEach((lesson: any) => {
        this.addLesson(moduleGroup.get("lessons") as FormArray, lesson);
      });
    }

    this.getFormArray("modules").push(moduleGroup);
  }

  addLesson(lessonsArray: FormArray, lesson: any = {}) {
    const lessonGroup = this.fb.group({
      title: [lesson.title || "", Validators.required],
      description: [lesson.description || ""],
      orderIndex: [lesson.orderIndex || lessonsArray.length],
      duration: [lesson.duration || null],
      type: [lesson.type || LessonType.TEXT],
      content: [lesson.content || ""],
      videoUrl: [lesson.videoUrl || ""],
      objectives: [lesson.objectives || ""],
      resources: [lesson.resources || ""],
      exercises: [lesson.exercises || ""],
      quizzes: [lesson.quizzes || ""],
      assignments: [lesson.assignments || ""],
    });
    lessonsArray.push(lessonGroup);
  }

  removeItem(arrayName: string, index: number) {
    this.getFormArray(arrayName).removeAt(index);
  }

  onSubmit() {
    if (this.courseForm.valid) {
      this.isLoading = true;
      const courseData = this.prepareCourseData();

      const request =
        this.isEditMode && this.courseId
          ? this.courseService.updateCourse(this.courseId, courseData)
          : this.courseService.createCourse(courseData, 1); // Assuming instructor ID is 1

      request.subscribe({
        next: (course) => {
          this.notificationService.showNotification(
            "Course updated successfully",
            "success"
          );
          this.router.navigate(["/courses"]);
        },
        error: (error) => {
          console.error("Error saving course:", error);
          this.notificationService.showNotification(
            "Error saving course",
            "danger"
          );
        },
      });
    }
  }

  private prepareCourseData(): Course {
    return {
      title: this.courseForm.value.title,
      description: this.courseForm.value.description,
      coverImage: this.courseForm.value.coverImage,
      category: this.courseForm.value.category,
      status: this.courseForm.value.status,
      type: this.courseForm.value.type,
      price: this.courseForm.value.price,
      duration: this.courseForm.value.duration,
      language: this.courseForm.value.language,
      level: this.courseForm.value.level,
      estimatedCompletionTime: this.courseForm.value.estimatedCompletionTime,
      targetAudience: this.courseForm.value.targetAudience,
      isAutomated: this.courseForm.value.isAutomated,
      difficultyScore: this.courseForm.value.difficultyScore,
      engagementScore: this.courseForm.value.engagementScore,
      completionRate: this.courseForm.value.completionRate,
      recommendationTags: this.courseForm.value.recommendationTags,
      aiGeneratedTags: this.courseForm.value.aiGeneratedTags,
      aiGeneratedSummary: this.courseForm.value.aiGeneratedSummary,
      learningObjectives: this.courseForm.value.learningObjectives,
      prerequisites: this.courseForm.value.prerequisites,
      skillWeights: this.courseForm.value.skillWeights,
    };
  }

  onCancel(): void {
    this.router.navigate(["/courses"]);
  }

  onFileChange(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onFileSelected(event: Event) {
    const file = (event.target as HTMLInputElement).files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = () => {
        this.courseForm.patchValue({
          coverImage: reader.result, // This will be the base64 string of the image
        });
      };
      reader.readAsDataURL(file);
    }
  }
}
