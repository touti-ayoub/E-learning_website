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
  timeLeft: number = 300; // 5 minutes in seconds
  interval: any;
  progress: number = 0; // Progress percentage
  isTimerRed: boolean = false; // Track if the timer should be red
  isReviewModalOpen: boolean = false; // Track if the review modal is open

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

    // Load saved state from localStorage
    const savedState = localStorage.getItem('quizState');
    if (savedState) {
      const state = JSON.parse(savedState);
      if (state.quizId === +quizId) {
        this.quiz = state.quiz;
        this.userAnswers = new Map(state.userAnswers);
        this.timeLeft = state.timeLeft;
        this.progress = state.progress;
        this.startTimer();
        return; // Exit early to avoid fetching and randomizing the quiz again
      }
    }

    // Fetch quiz if no saved state
    this.quizService.getQuizById(+quizId).subscribe(
      (quiz) => {
        this.quiz = quiz;
        if (!this.quiz.questions || this.quiz.questions.length === 0) {
          console.error('No questions found for this quiz');
        }
        // Randomize questions and answers only if no saved state
        this.quiz.questions = this.shuffleArray(this.quiz.questions);
        this.quiz.questions.forEach(question => {
          question.answers = this.shuffleArray(question.answers);
        });
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
    this.saveState(); // Save state on destroy
  }

  startTimer(): void {
    console.log('Starting timer with timeLeft:', this.timeLeft);
    this.interval = setInterval(() => {
      if (this.timeLeft > 0) {
        this.timeLeft--;
        if (this.timeLeft === 60) {
          this.isTimerRed = true; // Change timer color to red
        }
        this.saveState(); // Save state every second to ensure timer is updated
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

  saveState(): void {
    if (this.quiz) {
      const state = {
        quizId: this.quiz.id,
        quiz: this.quiz,
        userAnswers: Array.from(this.userAnswers.entries()),
        timeLeft: this.timeLeft,
        progress: this.progress
      };
      localStorage.setItem('quizState', JSON.stringify(state));
    }
  }
  

  clearState(): void {
    localStorage.removeItem('quizState');
  }

  onAnswerSelected(questionId: number | undefined, answerId: number | undefined): void {
    if (questionId !== undefined && answerId !== undefined) {
      this.userAnswers.set(questionId, answerId);
      this.updateProgress(); // Update progress when a question is answered
      this.saveState(); // Save state whenever an answer is selected
    } else {
      console.error('Question ID or Answer ID is undefined');
    }
  }

  updateProgress(): void {
    if (this.quiz && this.quiz.questions) {
      const totalQuestions = this.quiz.questions.length;
      const answeredQuestions = this.userAnswers.size;
      this.progress = Math.round((answeredQuestions / totalQuestions) * 100);
      console.log(`Progress: ${this.progress}%`);
      this.saveState(); // Save state whenever progress is updated
    }
  }

  validateAnswers(): boolean {
    if (this.quiz) {
      const unansweredQuestions = this.quiz.questions.filter(
        question => !this.userAnswers.has(question.id)
      );
      if (unansweredQuestions.length > 0) {
        alert('You have unanswered questions. Please review your answers before submitting.');
        return false;
      }
    }
    return true;
  }

  submitQuiz(): void {
    if (!this.validateAnswers()) {
      return;
    }

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
        this.clearState(); // Clear saved state after submission
        this.router.navigate(['/quiz-result', score, this.quiz!.questions.length]);
      },
      (error) => {
        console.error('Error evaluating quiz:', error);
        alert('Error evaluating quiz. Please try again.');
      }
    );
  }

  openReviewModal(): void {
    this.isReviewModalOpen = true;
  }

  closeReviewModal(): void {
    this.isReviewModalOpen = false;
  }

  getAnswerText(questionId: number): string | undefined {
    const answerId = this.userAnswers.get(questionId);
    const question = this.quiz?.questions.find(q => q.id === questionId);
    const answer = question?.answers.find(a => a.id === answerId);
    return answer?.text;
  }

  shuffleArray<T>(array: T[]): T[] {
    return array.sort(() => Math.random() - 0.5);
  }
}