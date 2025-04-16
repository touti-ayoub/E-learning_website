import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../services/Communications/post.service';
import { Post } from './post.model';
import { parseISO } from 'date-fns';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.css']
})
export class PostListComponent implements OnInit {
  @Input() forumId!: number;
  posts: Post[] = [];

  constructor(private route: ActivatedRoute, private router: Router, private postService: PostService) {}

  ngOnInit(): void {
    this.route.params.subscribe((params: any) => {
      this.forumId = +params['id'];
      this.loadPosts();
    });
  }

  loadPosts(): void {
    this.postService.getPostsByForum(this.forumId).subscribe(
      (data: Post[]) => {
        console.log('Raw posts from backend:', data); // Vérifiez les données reçues
        this.posts = data.map(post => ({
          ...post,
          datePost: new Date(post.datePost) // Conversion en objet Date
        }));
        console.log('Processed posts:', this.posts); // Vérifiez les données après conversion
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
}