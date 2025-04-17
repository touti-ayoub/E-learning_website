import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Import nécessaire pour le pipe 'date'
import { ActivatedRoute } from '@angular/router';
import { PostService } from 'src/service/mic3/post.service';
import { Post } from 'src/model/mic3/post.model';

@Component({
  selector: 'app-post-list',
  templateUrl: './post-list.component.html',
  styleUrls: ['./post-list.component.scss'],
  standalone: true,
  imports: [CommonModule] // Ajoutez CommonModule ici
})
export class PostListComponent implements OnInit {
  forumId!: number;
  posts: Post[] = [];
  isLoading: boolean = true;

  constructor(private route: ActivatedRoute, private postService: PostService) {}

  ngOnInit(): void {
    // Récupérer l'ID du forum depuis l'URL
    this.forumId = Number(this.route.snapshot.paramMap.get('forumId'));
    if (this.forumId) {
      this.loadPosts();
    }
  }

  loadPosts(): void {
    this.postService.getPostsByForum(this.forumId).subscribe({
      next: (data) => {
        this.posts = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching posts:', error);
        this.isLoading = false;
      }
    });
  }
}