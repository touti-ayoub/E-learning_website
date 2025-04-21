export interface Certificate {
  id: string;
  certificateUrl: string;
  issuedDate: Date;
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