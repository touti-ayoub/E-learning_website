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
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const userId = localStorage.getItem('id');
    if (userId) {
      this.userId = parseInt(userId, 10); // Convert the string to a number
      console.log('Logged-in user ID:', this.userId);

      // Fetch quizzes after retrieving the user ID
      this.quizService.getQuizzes().subscribe((quizzes) => {
        this.quizzes = quizzes;

        // Fetch the user's quiz results
        this.quizzes.forEach((quiz) => {
          if (quiz.userScores && quiz.userScores[this.userId!]) {
            quiz.result = quiz.userScores[this.userId!]; // Assign the logged-in user's score
            this.takenQuizzes.push(quiz.id); // Mark the quiz as taken
          }
        });

        console.log('Updated quizzes with results:', this.quizzes); // Debugging statement
      });
    } else {
      console.error('No logged-in user found in localStorage');
      alert('You must be logged in to view the quiz list.');
      this.authService.logout();
      this.router.navigate(['/login']);
    }
  }

  hasTakenQuiz(quizId: number): boolean {
    return this.takenQuizzes.includes(quizId);
  }
  
  takeQuiz(quizId: number): void {
    if (this.hasTakenQuiz(quizId)) {
      // Navigate to the quiz result page
      this.router.navigate(['/quiz-results', quizId]);
    } else {
      // Navigate to the quiz-taking page
      this.router.navigate(['/quiz-take', quizId]);
    }
  }
}