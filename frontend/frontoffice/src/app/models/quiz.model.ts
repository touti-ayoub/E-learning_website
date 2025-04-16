export interface Quiz {
  id: number;
  title: string;
  description: string;
  questions: QuizQuestion[];
  userIds: string[]; // Change to string[] if userIds store usernames
  userScores?: { [userId: number]: number }; // Map of user IDs to their scores
  result?: number; // Store the result of the quiz
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