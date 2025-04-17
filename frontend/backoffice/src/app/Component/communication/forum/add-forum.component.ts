import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ForumService } from 'src/service/mic3/forum.service';
import { Forum } from 'src/model/mic3/forum.model';
import { Router } from '@angular/router'; // Importez Router ici

@Component({
  selector: 'app-add-forum',
  templateUrl: './add-forum.component.html',
  styleUrls: ['./add-forum.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule] // Ajoutez ReactiveFormsModule ici
})
export class AddForumComponent {
  forumForm: FormGroup;
  isSubmitting: boolean = false;

  constructor(private fb: FormBuilder, private forumService: ForumService ,private router: Router // Injectez le service Router
) {
    this.forumForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]]
      
    });
  }

  onSubmit(): void {
    if (this.forumForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const newForum: Partial<Forum> = {
      title: this.forumForm.value.title,
      description: this.forumForm.value.description,
      dateCreation: new Date(),
      nbrPosts: 0,
      posts: []
    };

    this.forumService.createForum(newForum as Forum).subscribe({
      next: (createdForum) => {
        console.log('Forum created:', createdForum);
        this.isSubmitting = false;
        this.forumForm.reset();
        alert('Forum successfully created!');
        this.router.navigate(['/forum/list']); // Redirection après succès
      },
      error: (error) => {
        console.error('Error creating forum:', error);
        this.isSubmitting = false;
        alert('An error occurred while creating the forum.');
      }
    });
  }
}