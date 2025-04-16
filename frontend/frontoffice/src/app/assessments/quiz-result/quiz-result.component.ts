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
  username: string | null = null; // Track the logged-in user's username

  constructor(private route: ActivatedRoute, private router: Router) {}

  ngOnInit() {
    const scoreParam = this.route.snapshot.paramMap.get('score');
    const totalParam = this.route.snapshot.paramMap.get('total');
  
    console.log('Score parameter:', scoreParam); // Debugging statement
    console.log('Total parameter:', totalParam); // Debugging statement
  
    this.score = scoreParam !== null ? +scoreParam : undefined;
    this.totalQuestions = totalParam !== null ? +totalParam : undefined;
  
    console.log('Parsed score:', this.score); // Debugging statement
    console.log('Parsed totalQuestions:', this.totalQuestions); // Debugging statement
  
    this.username = localStorage.getItem('username');
    console.log('Logged-in username:', this.username); // Debugging statement
  
    if (this.score === undefined || this.totalQuestions === undefined) {
      console.error('Invalid score or total questions parameter');
      this.router.navigate(['/quizzes/list']);
    }
  }
}