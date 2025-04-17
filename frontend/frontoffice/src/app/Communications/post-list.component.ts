import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../services/Communications/post.service';
import { Post } from './post.model';
import { Interaction, InteractionType } from '../Communications/interraction/interaction.model';
import { InteractionService } from '../../services/Communications/interaction.service';
import { faThumbsUp, faThumbsDown } from '@fortawesome/free-solid-svg-icons';
import { HttpClient } from '@angular/common/http';
import { faTrash } from '@fortawesome/free-solid-svg-icons';



@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit {
  @Input() forumId!: number;
  posts: Post[] = [];
  idPost!: number;
  faThumbsUp = faThumbsUp;
  faThumbsDown = faThumbsDown;
  faTrash = faTrash;
  
  constructor(private route: ActivatedRoute,
     private router: Router,
      private postService: PostService,
      private interactionService: InteractionService,
      private http: HttpClient) {}

  ngOnInit(): void {
    this.route.params.subscribe((params: any) => {
      this.forumId = +params['id'];
      this.loadPosts();
    });
  }

  loadPosts(): void {
    this.postService.getPostsByForum(this.forumId).subscribe(
      (data: Post[]) => {
        this.posts = data.map(post => ({
          ...post,
          datePost: new Date(post.datePost), // Conversion en objet Date
          newComment: '', // Initialisez newComment à une chaîne vide
          comments: post.comments || [], // Assurez-vous que comments est une liste
          likeCount: post.likeCount || 0, // Assurez-vous que likeCount est défini
          dislikeCount: post.dislikeCount || 0 // Assurez-vous que dislikeCount est défini
        }));
        console.log('Posts loaded:', this.posts); // Vérifiez les données chargées
      },
      (error: any) => {
        console.error('Error fetching posts:', error);
      }
    );
  }

  onAddPost(): void {
    // Redirige vers la page d'ajout de post avec l'ID du forum
    this.router.navigate(['/add-post'], { queryParams: { forumId: this.forumId } });
  }
  onUpdatePost(post: Post): void {
    console.log('Post to update:', post); // Vérifiez l'objet post
    console.log('Post ID:', post.idPost); // Vérifiez l'ID du post
    this.router.navigate(['/update-post'], { queryParams: { idPost: post.idPost, forumId: post.forumId } });
  }
  onDeletePost(idPost: number): void {
    console.log('Deleting post with ID:', idPost);
    if (confirm('Are you sure you want to delete this post?')) {
      this.postService.deletePost(idPost).subscribe(
        () => {
          console.log(`Post with ID ${idPost} deleted successfully.`);
          // Rechargez la liste des posts après suppression
          this.loadPosts();
        },
        (error) => {
          console.error('Error deleting post:', error);
        }
      );
    }
}
onLikePost(postId: number): void {
  const url = `http://localhost:8088/mic3/interactions/like/${postId}`;
  this.http.post(url, {}).subscribe({
    next: () => {
      const post = this.posts.find(p => p.idPost === postId);
      if (post) {
        post.likeCount += 1; // Mettez à jour localement
      }
    },
    error: (error: any) => {
      console.error('Error liking post:', error);
    }
  });
}
onDislikePost(postId: number): void {
  const url = `http://localhost:8088/mic3/interactions/dislike/${postId}`;
  this.http.post(url, {}).subscribe({
    next: () => {
      const post = this.posts.find(p => p.idPost === postId);
      if (post) {
        post.dislikeCount += 1; // Mettez à jour localement
      }
    },
    error: (error: any) => {
      console.error('Error disliking post:', error);
    }
  });
}
onAddComment(postId: number, comment: string): void {
  if (!comment.trim()) {
    console.warn('Comment is empty.');
    return;
  }

  const url = `http://localhost:8088/mic3/interactions/comment/${postId}`;
  this.http.post<Interaction>(url, comment, { responseType: 'json' }).subscribe({
    next: (response: Interaction) => {
      console.log('Comment added:', response);
      const post = this.posts.find(p => p.idPost === postId);
      if (post) {
        if (!post.comments) {
          post.comments = []; // Initialisez comments si ce n'est pas défini
        }
        post.comments.push(response); // Ajoutez le commentaire à la liste
        post.newComment = ''; // Réinitialisez le champ de commentaire
      }
    },
    error: (error: any) => {
      console.error('Error:', error);
    }
  });
}
onDeleteComment(postId: number, commentId: number): void {
  const url = `http://localhost:8088/mic3/interactions/comment/${commentId}`;
  if (confirm('Are you sure you want to delete this comment?')) {
    this.http.delete(url).subscribe({
      next: () => {
        console.log(`Comment with ID ${commentId} deleted successfully.`);
        const post = this.posts.find(p => p.idPost === postId);
        if (post) {
          post.comments = post.comments.filter(comment => comment.idInteraction !== commentId);
        }
      },
      error: (error: any) => {
        console.error('Error deleting comment:', error);
      }
    });
  }
}
}
