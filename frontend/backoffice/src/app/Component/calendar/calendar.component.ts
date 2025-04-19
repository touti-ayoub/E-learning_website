import { Component, OnInit } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  standalone: true,
  styleUrls: ['./calendar.component.scss']
})
export class CalendarComponent implements OnInit {
  calendarUrl: SafeResourceUrl;

  constructor(private sanitizer: DomSanitizer) {}

  ngOnInit(): void {
    this.calendarUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
      'https://calendar.google.com/calendar/embed?src=db5783463b512af762897b63c4d31b3ddbc6a07a52169598009f21ec6f5543e2%40group.calendar.google.com&ctz=Africa%2FTunis'
    );
  }
}
