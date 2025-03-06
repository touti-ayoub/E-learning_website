import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../services/quiz.service';
import { Quiz } from '../../models/quiz.model';

@Component({
  selector: 'app-quiz-take',
  templateUrl: './quiz-take.component.html',
  styleUrls: ['./quiz-take.component.css']
})
export class QuizTakeComponent implements OnInit, OnDestroy {
  quiz: Quiz | null = null;
  userAnswers: Map<number, number> = new Map();
  timeLeft: number = 5; // 5 minutes in seconds
  interval: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService
  ) {}

  ngOnInit(): void {
    const quizId = this.route.snapshot.paramMap.get('id');

    if (quizId === null) {
      console.error('Quiz ID is missing from the URL');
      this.router.navigate(['/quizzes/list']);
      return;
    }

    this.quizService.getQuizById(+quizId).subscribe(
      (quiz) => {
        this.quiz = quiz;
        if (!this.quiz.questions || this.quiz.questions.length === 0) {
          console.error('No questions found for this quiz');
        }
        this.startTimer();
      },
      (error) => {
        console.error('Error fetching quiz:', error);
        this.router.navigate(['/quizzes/list']);
      }
    );
  }

  ngOnDestroy(): void {
    if (this.interval) {
      clearInterval(this.interval);
    }
  }

  startTimer(): void {
    console.log('Starting timer with timeLeft:', this.timeLeft);
    this.interval = setInterval(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
        console.log('Time left:', this.timeLeft);
      } else {
        console.log('Time is up, submitting quiz');
        this.submitQuiz();
      }
    }, 1000);
  }

  formatTime(seconds: number): string {
    const minutes: number = Math.floor(seconds / 60);
    const remainingSeconds: number = seconds % 60;
    return `${this.pad(minutes)}:${this.pad(remainingSeconds)}`;
  }

  pad(num: number): string {
    return num < 10 ? '0' + num : num.toString();
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