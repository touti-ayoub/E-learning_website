// src/app/Communications/forum-list.component.ts
import { Component, OnInit } from '@angular/core';
import { ForumService } from '../../services/Communications/forum.sevice';
import { Forum } from './forum.model';

@Component({
  selector: 'app-forum-list',
  templateUrl: './forum-list.component.html',
  styleUrls: ['./forum-list.component.css']
})
export class ForumListComponent implements OnInit {
  forums: Forum[] = [];

  constructor(private forumService: ForumService) {}

  ngOnInit(): void {
    this.forumService.getForums().subscribe(
      (data: Forum[]) => {
        console.log('Forums fetched from API:', data);
        this.forums = data;
      },
      (error: any) => {
        console.error('Error fetching forums:', error);
      }
    );
  }
}
