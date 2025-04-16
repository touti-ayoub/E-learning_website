import { QuizQuestion } from "./quiz-question.model";

// File: src/app/models/quiz.model.ts
export interface Quiz {
  id: number;
  title: string;
  description: string;
  questions?: QuizQuestion[]; // Optional array of questions
}