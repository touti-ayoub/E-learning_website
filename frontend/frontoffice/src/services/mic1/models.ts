export interface Course {
  id?: number;
  title: string;
  description?: string;
  price: number;
  free?: boolean;
  coverImageData?: string;
  lessons?: Lesson[];
  createdAt?: Date;
  updatedAt?: Date;
  categoryId: number;
}

export interface Lesson {
  id?: number;
  title: string;
  content?: string;
  videoUrl?: string;
  videoType?: string;
  pdfUrl?: string;
  pdfName?: string;
  presentationUrl?: string;
  presentationName?: string;
  orderIndex?: number;
  courseId?: number;
}

export interface Category {
  id?: number;
  name: string;
  description?: string;
  coverImageData?: string;
} 