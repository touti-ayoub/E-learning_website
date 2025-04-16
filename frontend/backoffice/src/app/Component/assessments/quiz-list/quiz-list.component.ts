import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Quiz } from 'src/model/mic/quiz.model';
import { QuizService } from 'src/service/mic/quiz.service';

@Component({
  selector: 'app-quiz-list',
  templateUrl: './quiz-list.component.html',
  styleUrls: ['./quiz-list.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule],
})
export class QuizListComponent implements OnInit {
  quizzes: Quiz[] = []; // Array to store quizzes
  selectedQuiz: Quiz | null = null; // Store the selected quiz for inline viewing
  editingQuiz: Quiz | null = null; // Store the quiz being edited

  constructor(private quizService: QuizService) {}

  ngOnInit(): void {
    this.loadQuizzes(); // Load quizzes when the component initializes
  }

  // Fetch the list of quizzes
  loadQuizzes(): void {
    this.quizService.getAllQuizzes().subscribe(
      (data) => {
        this.quizzes = data;
        console.log('Quizzes loaded:', this.quizzes);
      },
      (error) => {
        console.error('Error loading quizzes:', error);
      }
    );
  }

  // Delete a quiz
  deleteQuiz(id: number): void {
    if (confirm('Are you sure you want to delete this quiz?')) {
      this.quizService.deleteQuiz(id).subscribe(
        () => {
          console.log('Quiz deleted:', id);
          this.loadQuizzes(); // Reload the list after deletion
        },
        (error) => {
          console.error('Error deleting quiz:', error);
        }
      );
    }
  }

  // Edit a quiz (inline editing)
  editQuiz(id: number): void {
    this.quizService.getQuizById(id).subscribe(
      (data) => {
        this.editingQuiz = { ...data }; // Clone the quiz to avoid modifying the original
        console.log('Editing quiz:', this.editingQuiz);
      },
      (error) => {
        console.error('Error loading quiz for editing:', error);
      }
    );
  }

  // Save the updated quiz
  saveQuiz(): void {
    if (this.editingQuiz) {
      this.quizService.updateQuiz(this.editingQuiz).subscribe(
        () => {
          console.log('Quiz updated successfully');
          this.editingQuiz = null; // Clear the editing state
          this.loadQuizzes(); // Reload the list
        },
        (error) => {
          console.error('Error updating quiz:', error);
        }
      );
    }
  }

  // Cancel editing
  cancelEdit(): void {
    this.editingQuiz = null; // Clear the editing state
  }

  // View a quiz (display details inline)
  viewQuiz(id: number): void {
    this.quizService.getQuizById(id).subscribe(
      (data) => {
        this.selectedQuiz = data;
        console.log('Selected quiz:', this.selectedQuiz);
      },
      (error) => {
        console.error('Error loading quiz:', error);
      }
    );
  }

  // Close the quiz details view
  closeQuizDetails(): void {
    this.selectedQuiz = null;
  }
}