import { Post } from './post.model';
export interface Forum {
  idForum: number;
  title: string;
  description: string;
  dateCreation: any;
  nbrPosts: number;
  posts: Post[];
  
}
