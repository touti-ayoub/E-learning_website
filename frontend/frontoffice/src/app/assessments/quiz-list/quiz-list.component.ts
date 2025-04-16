import { Component, OnInit } from '@angular/core';
import { QuizService } from '../../services/quiz.service';
import { AuthService } from '../../services/auth.service'; // Import AuthService
import { Quiz } from '../../models/quiz.model';
import { Router } from '@angular/router'; // Import Router

@Component({
  selector: 'app-quiz-list',
  templateUrl: './quiz-list.component.html',
  styleUrls: ['./quiz-list.component.css']
})
export class QuizListComponent implements OnInit {
  quizzes: Quiz[] = [];
  userId: number | null = null; // Track the logged-in user's ID
  takenQuizzes: number[] = []; // Store IDs of quizzes the user has taken

  constructor(
    private quizService: QuizService,
    private authService: AuthService, // Inject AuthService
    private router: Router // Inject Router
  ) {}

  ngOnInit() {
    const userId = localStorage.getItem('id');
    if (userId) {
      this.userId = parseInt(userId, 10); // Convert the string to a number
      console.log('Logged-in user ID:', this.userId); // Debugging statement
  
      // Fetch quizzes after retrieving the user ID
      this.quizService.getQuizzes().subscribe((quizzes) => {
        this.quizzes = quizzes;
  
        // Identify quizzes the user has taken
        if (this.userId !== null) {
          this.takenQuizzes = quizzes
            .filter((quiz) => Array.isArray(quiz.userIds) && this.userId !== null && quiz.userIds.includes(this.userId.toString())) // Ensure userIds is an array and userId is non-null
            .map((quiz) => quiz.id);
          console.log('Taken quizzes:', this.takenQuizzes); // Debugging statement
        }
      });
    } else {
      console.error('No logged-in user found in localStorage'); // Debugging statement
      alert('You must be logged in to view the quiz list.');
      this.authService.logout(); // Clear any invalid session
      this.router.navigate(['/login']); // Redirect to login page
    }
  }

  hasTakenQuiz(quizId: number): boolean {
    return this.takenQuizzes.includes(quizId);
  }
}