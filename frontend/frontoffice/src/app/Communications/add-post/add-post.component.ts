import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../../services/Communications/post.service';
import { Post } from '../post.model';

@Component({
  selector: 'app-add-post',
  templateUrl: './add-post.component.html',
  styleUrls: ['./add-post.component.css']
})
export class AddPostComponent implements OnInit {
  postContent: string = '';
  forumId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    // Récupère l'ID du forum depuis les queryParams
    this.route.queryParams.subscribe((params) => {
      this.forumId = +params['forumId'];
    });
  }

  onSubmit(): void {
    if (this.postContent.trim()) {
      // Créez un objet Post avec la date actuelle et des compteurs initialisés
      const newPost: Post = {
        idPost: 0, // ID sera généré par le backend
        content: this.postContent,
        datePost: new Date(), // Date du système
        forumId: this.forumId,
        likeCount: 0, // Initialisez à 0
        dislikeCount: 0, // Initialisez à 0
        comments: [] // Initialisez à un tableau vide
      };
  
      // Appelez le service pour ajouter le post
      this.postService.addPost(newPost, this.forumId).subscribe(
        (response) => {
          console.log('Post added successfully:', response);
          // Redirigez vers la liste des posts du forum
          this.router.navigate([`/forum/${this.forumId}/posts`]);
        },
        (error) => {
          console.error('Error adding post:', error);
        }
      );
    }
  }
}