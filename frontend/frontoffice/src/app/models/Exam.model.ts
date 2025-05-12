export interface Certificate {
  id: number;
  examId: number;
  url: string;
  issueDate: Date;
  expiryDate?: Date;
}

export interface Exam {
  id: string;
  title: string;
  description: string;
  examDate: Date;
  status: 'CREATED' | 'SUBMITTED' | 'PASSED' | 'FAILED';
  score?: number;
  examFileUrl?: string;
  submittedFileUrl?: string;
  userId: string;
  certificateGenerated:any
  certificate?: Certificate;
}

export interface ExamDTO {
  title: string;
  description: string;
  examDate: Date;
  userId: string;
}

export interface ScoreDTO {
  score: number;
}