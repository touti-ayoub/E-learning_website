import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PostService } from '../../../../services/Communications/post.service';
import { Post } from '../../post.model';
import { post } from 'jquery';
import { Interaction } from '../../../Communications/interraction/interaction.model';

@Component({
  selector: 'app-update-post',
  templateUrl: './update-post.component.html',
  styleUrls: ['./update-post.component.css']
})
export class UpdatePostComponent implements OnInit {
  postId!: number;
  postContent: string = '';
  forumId!: number; // ID du forum
  likeCount: number = 0; // Compteur de likes
  dislikeCount: number = 0; // Compteur de dislikes
  comments: Interaction[] = []; // Liste des commentaires associés

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      console.log('Query Params:', params); // Vérifiez les paramètres reçus
      this.postId = +params['idPost']; // Convertit en nombre
      this.forumId = +params['forumId']; // Convertit en nombre
      if (isNaN(this.postId)) {
        console.error('Invalid postId:', params['idPost']);
      }
      this.loadPost();
    });
  }

  loadPost(): void {
    if (isNaN(this.postId) || this.postId <= 0) {
      console.error('Invalid postId before loading post:', this.postId);
      return;
    }

    this.postService.getPostById(this.postId).subscribe(
      (post) => {
        console.log('Loaded post:', post); // Vérifiez les données du post
        this.postContent = post.content;
        this.forumId = post.forumId; // Assurez-vous que l'ID du forum est défini
        this.likeCount = post.likeCount; // Récupérez les likes
        this.dislikeCount = post.dislikeCount; // Récupérez les dislikes
        this.comments = post.comments; // Récupérez les commentaires
      },
      (error) => {
        console.error('Error loading post:', error);
      }
    );
  }

  onSubmit(): void {
    console.log('Submitting update for post ID:', this.postId);
    console.log('Updated content:', this.postContent);

    if (isNaN(this.postId) || this.postId <= 0) {
      console.error('Invalid postId:', this.postId);
      return;
    }

    const updatedPost: Post = {
      idPost: this.postId,
      content: this.postContent,
      datePost: new Date(), // Met à jour la date si nécessaire
      forumId: this.forumId, // Assurez-vous que l'ID du forum est défini
      likeCount: this.likeCount, // Utilisez les valeurs récupérées
      dislikeCount: this.dislikeCount, // Utilisez les valeurs récupérées
      comments: this.comments // Réutilisez les commentaires existants
    };

    this.postService.updatePost(this.postId, updatedPost).subscribe(
      () => {
        console.log('Post updated successfully.');
        this.router.navigate(['/forum', this.forumId, 'posts']);
      },
      (error) => {
        console.error('Error updating post:', error);
      }
    );
  }
}