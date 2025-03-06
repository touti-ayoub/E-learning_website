import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { Quiz, QuizQuestion, QuizResult } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-take',
  templateUrl: './quiz-take.component.html',
  styleUrls: ['./quiz-take.component.css']
})
export class QuizTakeComponent implements OnInit {
  quiz: Quiz | null = null;
  userAnswers: Map<number, number> = new Map();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
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
        if (!this.quiz.questions || this.quiz.questions.length === 0) {
          console.error('No questions found for this quiz');
        }
      },
      (error) => {
        console.error('Error fetching quiz:', error);
        this.router.navigate(['/quizzes/list']); // Redirect to quiz list on error
      }
    );
  }

  onAnswerSelected(questionId: number | undefined, answerId: number | undefined): void {
    if (questionId !== undefined && answerId !== undefined) {
      this.userAnswers.set(questionId, answerId);
    } else {
      console.error('Question ID or Answer ID is undefined');
    }
  }

  submitQuiz(): void {
    if (!this.quiz) {
      console.error('Quiz is not loaded');
      return;
    }

    const quizId = this.quiz.id;
    if (quizId === undefined) {
      console.error('Quiz ID is undefined');
      return;
    }

    // Convert Map to an object
    const userAnswersObject = Object.fromEntries(this.userAnswers);
    console.log('Submitting quiz with answers:', userAnswersObject);

    this.quizService.evaluateQuiz(quizId, userAnswersObject).subscribe(
      (score) => {
        alert(`Your score: ${score}/${this.quiz!.questions.length}`);
        // Navigate to the quiz result page
        this.router.navigate(['/quiz-result', score, this.quiz!.questions.length]);
      },
      (error) => {
        console.error('Error evaluating quiz:', error);
        alert('Error evaluating quiz. Please try again.');
      }
    );
  }
}