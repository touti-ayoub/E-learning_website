export interface Exam {
    id?: number;
    title: string;
    description: string;
    date: Date;
    userId: number;
    score?: number;
    passed?: boolean;
    status: ExamStatus;
    examFileUrl?: string;
    submittedFileUrl?: string;
    certificateGenerated?: boolean;
    certificateUrl?: string;
    certificate?: Certificate;
}

export enum ExamStatus {
    CREATED = 'CREATED',
    SUBMITTED = 'SUBMITTED',
    GRADED = 'GRADED',
    PASSED = 'PASSED',
    FAILED = 'FAILED'
}

export interface Certificate {
    id?: number;
    certificateUrl: string;
    issuedDate: Date;
} 