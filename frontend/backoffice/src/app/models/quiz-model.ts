export interface Quiz {
  idQuiz: number;
  courseId: number;
  quizName: string;
  totalQuestion: number;
  passingScore: number;
    questions: QuizQuestion[];
    results : QuizResult[];
  }
  
  export interface QuizQuestion {
    idQuestion: number;
    questionText: string;
    options: string[];
    correctAnswer: string;
    id_quiz: number;
  }
  
  export interface QuizResult {
    idResult: number;
    idUser: number;
    id_quiz : number;
    score: number;
    dateTaken: Date;
    passed : boolean;
  }