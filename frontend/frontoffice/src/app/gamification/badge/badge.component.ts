import { Component, OnInit } from '@angular/core';
import { BadgeService } from '../../../services/gamification/badge.service';
import { Badge } from '../../models/models';

@Component({
  selector: 'app-badge',
  templateUrl: './badge.component.html',
  styleUrls: ['./badge.component.css']
})
export class BadgeComponent implements OnInit {
  badges: Badge[] = [];

  constructor(private badgeService: BadgeService) {}

  ngOnInit(): void {
    this.badgeService.getBadges().subscribe(data => {
      this.badges = data;
    });
  }
}
