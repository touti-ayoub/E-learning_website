import { Interaction } from '../Communications/interraction/interaction.model';
export interface Post {
  idPost: number;
  content: string;
  datePost: Date;
  forumId: number;
  likeCount: number;
  dislikeCount: number;
  newComment?: string; // Champ temporaire pour le commentaire
  comments: Interaction[]; // Liste des commentaires associés
}