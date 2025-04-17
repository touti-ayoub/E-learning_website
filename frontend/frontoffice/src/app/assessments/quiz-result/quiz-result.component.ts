import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-quiz-result',
  templateUrl: './quiz-result.component.html',
  styleUrls: ['./quiz-result.component.css']
})
export class QuizResultComponent implements OnInit {
  score: number | undefined;
  totalQuestions: number | undefined;

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    const scoreParam = this.route.snapshot.paramMap.get('score');
    const totalParam = this.route.snapshot.paramMap.get('total');

    // Convert parameters to numbers and handle null/undefined cases
    this.score = scoreParam !== null ? +scoreParam : undefined;
    this.totalQuestions = totalParam !== null ? +totalParam : undefined;

    // Redirect if parameters are invalid
    if (this.score === undefined || this.totalQuestions === undefined) {
      console.error('Invalid score or total questions parameter');
      this.router.navigate(['/quizzes/list']);
    }
  }
}