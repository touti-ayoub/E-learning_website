export interface Quiz {
  id: number;
  title: string;
  description: string;
  questions: QuizQuestion[];
  userIds: string[]; // Change to string[] if userIds store usernames
}

export interface QuizQuestion {
  id: number;
  text: string;
  answers: QuizAnswer[];
}

export interface QuizAnswer {
  id: number;
  text: string;
  isCorrect: boolean;
}