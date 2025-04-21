export interface Exam {
    id: number;
    title: string;
    description: string;
    date: Date;
    userId: number;
    score?: number;
    passed?: boolean;
    examFileUrl?: string;
    submittedFileUrl?: string;
    certificate?: Certificate;
  }
  
  export interface Certificate {
    id: number;
    certificateUrl: string;
    issuedDate: Date;
  }
  
  export interface ExamDTO {
    title: string;
    description: string;
    date: Date;
    userId: number;
  }
  
  export interface ScoreDTO {
    score: number;
  }