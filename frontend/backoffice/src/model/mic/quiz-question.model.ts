// File: src/model/mic/quiz-question.model.ts
import { QuizResult } from './quiz-result.model';

export interface QuizQuestion {
  id: number;
  text: string;
  answers: QuizResult[]; // List of possible answers for the question
}