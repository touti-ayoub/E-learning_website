import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { Quiz, QuizQuestion } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-take',
  templateUrl: './quiz-take.component.html',
  styleUrls: ['./quiz-take.component.css']
})
export class QuizTakeComponent implements OnInit {
  quiz: Quiz | null = null; // Initialize quiz as null
  userAnswers: Map<number, number> = new Map();

  constructor(
    private route: ActivatedRoute,
    private quizService: QuizService,
    private router: Router // Inject Router for navigation
  ) {}

  ngOnInit() {
    const quizId = this.route.snapshot.paramMap.get('id');

    // Check if quizId is null
    if (quizId === null) {
      console.error('Quiz ID is missing from the URL');
      this.router.navigate(['/quizzes/list']); // Redirect to quiz list
      return;
    }

    // Fetch the quiz by ID
    this.quizService.getQuizById(+quizId).subscribe(
      (quiz) => {
        this.quiz = quiz;
      },
      (error) => {
        console.error('Error fetching quiz:', error);
        this.router.navigate(['/quizzes/list']); // Redirect to quiz list on error
      }
    );
  }

  submitQuiz() {
    if (!this.quiz) {
      console.error('Quiz is not loaded');
      return;
    }
  
    const quizId = this.quiz.id;
    if (quizId === undefined) {
      console.error('Quiz ID is undefined');
      return;
    }
  
    this.quizService.evaluateQuiz(quizId, this.userAnswers).subscribe(
      (score) => {
        alert(`Your score: ${score}/${this.quiz!.quizQuestions.length}`);
        // Navigate to the quiz result page
        this.router.navigate(['/quiz-result', score, this.quiz!.quizQuestions.length]);
      },
      (error) => {
        console.error('Error evaluating quiz:', error);
        alert('Error evaluating quiz. Please try again.');
      }
    );
  }
  onAnswerSelected(questionId: number | undefined, answerId: number | undefined) {
    if (questionId !== undefined && answerId !== undefined) {
      this.userAnswers.set(questionId, answerId);
    }
  }
}