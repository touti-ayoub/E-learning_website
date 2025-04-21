export interface Exam {
  id: number;
  title: string;
  description: string;
  date: string;
  userId: number;
  examFileUrl?: string;
  submittedFileUrl?: string;
  score?: number;
  passed?: boolean;
  status?: 'pending' | 'submitted' | 'graded';
}