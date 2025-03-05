export enum CourseType {
  STATIC = "STATIC",
  DYNAMIC = "DYNAMIC",
  HYBRID = "HYBRID",
}

export enum CourseStatus {
  DRAFT = "DRAFT",
  PUBLISHED = "PUBLISHED",
  ARCHIVED = "ARCHIVED",
}

export enum ModuleType {
  THEORY = "THEORY",
  PRACTICE = "PRACTICE",
  ASSESSMENT = "ASSESSMENT",
  PROJECT = "PROJECT",
  INTERACTIVE = "INTERACTIVE",
}

export enum LessonType {
  VIDEO = "VIDEO",
  TEXT = "TEXT",
  QUIZ = "QUIZ",
  ASSIGNMENT = "ASSIGNMENT",
  PROJECT = "PROJECT",
  DISCUSSION = "DISCUSSION",
  INTERACTIVE = "INTERACTIVE",
}

export enum ProgressStatus {
  NOT_STARTED = "NOT_STARTED",
  IN_PROGRESS = "IN_PROGRESS",
  PAUSED = "PAUSED",
  COMPLETED = "COMPLETED",
}

export interface LearningObjective {
  id?: number;
  objective: string;
  description?: string;
  difficultyLevel?: number;
  completionThreshold?: number;
  isAiGenerated?: boolean;
}

export interface PrerequisiteSkill {
  id?: number;
  skillName: string;
  description?: string;
  proficiencyLevel?: number;
  isRequired?: boolean;
  weightInCourse?: number;
}

export interface CourseModule {
  id?: number;
  title: string;
  description?: string;
  orderIndex: number;
  duration?: number;
  completionRate?: number;
  difficultyLevel?: number;
  learningObjectives?: string;
  prerequisites?: string;
  recommendedPath?: string;
  type: ModuleType;
  lessons?: Lesson[];
  createdAt?: Date;
  updatedAt?: Date;
}

export interface Lesson {
  id?: number;
  title: string;
  description?: string;
  orderIndex: number;
  duration?: number;
  videoUrl?: string;
  content?: string;
  objectives?: string;
  prerequisites?: string;
  resources?: string;
  exercises?: string;
  quizzes?: string;
  assignments?: string;
  adaptivePath?: string;
  recommendedPreparation?: string;
  averageCompletionTime?: number;
  difficultyScore?: number;
  type: LessonType;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface CourseProgress {
  id?: number;
  progressPercentage: number;
  score?: number;
  timeSpentMinutes?: number;
  currentModuleId?: string;
  currentLessonId?: string;
  startedAt?: Date;
  completedAt?: Date;
  lastAccessDate?: Date;
  isCompleted?: boolean;
  status: ProgressStatus;
  learningStyle?: string;
  engagementScore?: number;
  performanceScore?: number;
  strengthAreas?: string;
  weaknessAreas?: string;
  personalizedRecommendations?: string;
  completedModules?: CourseModule[];
}

export interface Course {
  id?: number;
  title: string;
  description: string;
  coverImage?: string;
  category: string;
  status: CourseStatus;
  type: CourseType;
  price: number;
  duration?: number;
  language: string;
  level: string;
  estimatedCompletionTime?: number;
  targetAudience?: string;
  isAutomated?: boolean;
  automatedFeatures?: any;
  difficultyScore?: number;
  engagementScore?: number;
  completionRate?: number;
  recommendationTags?: string;
  aiGeneratedTags?: string[];
  aiGeneratedSummary?: string;
  rating?: number;
  instructor?: {
    id: number;
    username: string;
    fullName: string;
    profilePicture?: string;
  };
  modules?: CourseModule[];
  enrolledUsers?: any[];
  userProgress?: CourseProgress[];
  learningObjectives?: LearningObjective[];
  prerequisites?: PrerequisiteSkill[];
  skillWeights?: { [key: string]: number };
  createdAt?: Date;
  updatedAt?: Date;
}
