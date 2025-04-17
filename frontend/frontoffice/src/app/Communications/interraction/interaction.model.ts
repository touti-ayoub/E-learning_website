import { Post } from '../post.model';
export interface Interaction {
    idInteraction: number;
    contentInteraction: string;
    dateInteraction: Date;
    postId: number; // ID du post associé
    typeInteraction: InteractionType;
    post?: Post; // Ajoutez cette propriété si le backend renvoie un objet `Post`
  }
  
  export enum InteractionType {
    LIKE = 'LIKE',
    DISLIKE = 'DISLIKE',
    COMMENT = 'COMMENT' // Ajoutez ce type pour les commentaires
  }