export interface Quiz {
  id: number;
  title: string;
  description: string;
  questions: QuizQuestion[];
}

export interface QuizQuestion {
  id: number;
  text: string;
  answers: QuizResult[];
}

export interface QuizResult {
  id: number;
  text: string;
  isCorrect: boolean;
}