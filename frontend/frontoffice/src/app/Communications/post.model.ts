export interface Post {
  idPost: number;
  content: string;
  datePost: Date; // Assurez-vous que c'est bien de type Date
  forumId: number;
}