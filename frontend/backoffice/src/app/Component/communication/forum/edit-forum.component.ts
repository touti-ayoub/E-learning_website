import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ForumService } from 'src/service/mic3/forum.service';
import { Forum } from 'src/model/mic3/forum.model';

@Component({
  selector: 'app-edit-forum',
  templateUrl: './edit-forum.component.html',
  styleUrls: ['./edit-forum.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule]
})
export class EditForumComponent implements OnInit {
  forumForm: FormGroup;
  isSubmitting: boolean = false;
  forumId!: number;

  constructor(
    private fb: FormBuilder,
    private forumService: ForumService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.forumForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  ngOnInit(): void {
    // Récupérer l'ID du forum depuis l'URL
    this.forumId = Number(this.route.snapshot.paramMap.get('idForum'));
    if (this.forumId) {
      this.loadForum();
    }
  }

  loadForum(): void {
    this.forumService.getForumById(this.forumId).subscribe({
      next: (forum) => {
        this.forumForm.patchValue({
          title: forum.title,
          description: forum.description
        });
      },
      error: (error) => {
        console.error('Error loading forum:', error);
        alert('An error occurred while loading the forum.');
      }
    });
  }

  onSubmit(): void {
    if (this.forumForm.invalid) {
      return;
    }

    this.isSubmitting = true;
    const updatedForum: Partial<Forum> = {
      title: this.forumForm.value.title,
      description: this.forumForm.value.description
    };

    this.forumService.updateForum(this.forumId, updatedForum as Forum).subscribe({
      next: () => {
        console.log('Forum updated successfully.');
        this.isSubmitting = false;
        alert('Forum successfully updated!');
        this.router.navigate(['/forum/list']);
      },
      error: (error) => {
        console.error('Error updating forum:', error);
        this.isSubmitting = false;
        alert('An error occurred while updating the forum.');
      }
    });
  }
}