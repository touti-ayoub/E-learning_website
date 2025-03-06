export interface Quiz {
  id?: number;
  title: string;
  description: string;
  quizQuestions: QuizQuestion[];
}

export interface QuizQuestion {
  id?: number;
  text: string;
  quizResults: QuizResult[];
}

export interface QuizResult {
  id?: number;
  text: string;
  isCorrect?: boolean;
}