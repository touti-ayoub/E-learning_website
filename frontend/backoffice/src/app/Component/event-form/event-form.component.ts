// src/app/Component/event-form/event-form.component.ts
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { EventDTO } from '../../../model/mic5/event-dto';
import { MatNativeDateModule, DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE, NativeDateAdapter } from '@angular/material/core';


function futureDateValidator() {
  return (control: any) => {
    if (!control.value) return null;

    // Handle both Date objects and string inputs
    const date = new Date(control.value);
    if (isNaN(date.getTime())) return { invalidDate: true };

    // Create comparison dates at midnight in local time
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const inputDate = new Date(date);
    inputDate.setHours(0, 0, 0, 0);

    return inputDate >= today ? null : { pastDate: true };
  };
}

@Component({
  selector: 'app-event-form',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatDialogModule
  ],
  providers: [
    { provide: DateAdapter, useClass: NativeDateAdapter },
    { provide: MAT_DATE_LOCALE, useValue: 'en-US' },
    {
      provide: MAT_DATE_FORMATS,
      useValue: {
        parse: {
          dateInput: 'MM/DD/YYYY',
        },
        display: {
          dateInput: 'MM/DD/YYYY',
          monthYearLabel: 'MMM YYYY',
          dateA11yLabel: 'LL',
          monthYearA11yLabel: 'MMMM YYYY',
        },
      },
    }
  ],
  template: `
    <h2 mat-dialog-title>{{ isEditMode ? 'Edit Event' : 'Create New Event' }}</h2>
    <form [formGroup]="eventForm" (ngSubmit)="onSubmit()">
      <!-- Updated template section -->
      <mat-dialog-content>
        <!-- Title (already correct) -->
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Title</mat-label>
          <input matInput formControlName="title" required>
          <mat-error *ngIf="eventForm.controls['title'].hasError('required')">Title is required</mat-error>
          <mat-error *ngIf="eventForm.controls['title'].hasError('minlength')">Title must be at least 3 characters</mat-error>
          <mat-error *ngIf="eventForm.controls['title'].hasError('maxlength')">Title cannot exceed 100 characters</mat-error>
        </mat-form-field>

        <!-- Description -->
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="4"></textarea>
          <mat-error *ngIf="eventForm.controls['description'].hasError('maxlength')">
            Description cannot exceed 1000 characters
          </mat-error>
        </mat-form-field>

        <!-- Start Date -->
        <div class="date-time-container">
          <mat-form-field appearance="fill" class="date-field">
            <mat-label>Start Date</mat-label>
            <input matInput [matDatepicker]="startPicker" formControlName="startDate" required>
            <mat-datepicker-toggle matSuffix [for]="startPicker"></mat-datepicker-toggle>
            <mat-datepicker #startPicker></mat-datepicker>
            <mat-error *ngIf="eventForm.controls['startDate'].hasError('required')">
              Date is required
            </mat-error>
            <mat-error *ngIf="eventForm.controls['startDate'].hasError('invalidDate')">
              Invalid date format (MM/DD/YYYY)
            </mat-error>
            <mat-error *ngIf="eventForm.controls['startDate'].hasError('pastDate')">
              Date must be today or in the future
            </mat-error>
          </mat-form-field>

          <!-- Start Time -->
          <mat-form-field appearance="fill" class="time-field">
            <mat-label>Start Time</mat-label>
            <input matInput type="time" formControlName="startTime" required>
            <mat-error *ngIf="eventForm.controls['startTime'].hasError('required')">Time is required</mat-error>
          </mat-form-field>
        </div>

        <!-- End Date -->
        <div class="date-time-container">
          <mat-form-field appearance="fill" class="date-field">
            <mat-label>End Date</mat-label>
            <input matInput [matDatepicker]="endPicker" formControlName="endDate" required>
            <mat-datepicker-toggle matSuffix [for]="endPicker"></mat-datepicker-toggle>
            <mat-datepicker #endPicker></mat-datepicker>
            <mat-error *ngIf="eventForm.controls['endDate'].hasError('required')">
              Date is required
            </mat-error>
            <mat-error *ngIf="eventForm.controls['endDate'].hasError('invalidDate')">
              Invalid date format (MM/DD/YYYY)
            </mat-error>
            <mat-error *ngIf="eventForm.controls['endDate'].hasError('pastDate')">
              Date must be today or in the future
            </mat-error>
          </mat-form-field>

          <!-- End Time -->
          <mat-form-field appearance="fill" class="time-field">
            <mat-label>End Time</mat-label>
            <input matInput type="time" formControlName="endTime" required>
            <mat-error *ngIf="eventForm.controls['endTime'].hasError('required')">Time is required</mat-error>
          </mat-form-field>
        </div>

        <!-- Place -->
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Place</mat-label>
          <input matInput formControlName="place" required>
          <mat-error *ngIf="eventForm.controls['place'].hasError('required')">Place is required</mat-error>
          <mat-error *ngIf="eventForm.controls['place'].hasError('minlength')">Place must be at least 2 characters</mat-error>
          <mat-error *ngIf="eventForm.controls['place'].hasError('maxlength')">Place cannot exceed 200 characters</mat-error>
        </mat-form-field>

        <!-- Event Type -->
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Event Type</mat-label>
          <mat-select formControlName="eventType" required>
            <mat-option value="HACKATHON">Hackathon</mat-option>
            <mat-option value="WORKSHOP">Workshop</mat-option>
            <mat-option value="SEMINAR">Seminar</mat-option>
            <mat-option value="WEBINAR">Webinar</mat-option>
          </mat-select>
          <mat-error *ngIf="eventForm.controls['eventType'].hasError('required')">Event type is required</mat-error>
        </mat-form-field>

        <!-- Maximum Capacity -->
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Maximum Capacity</mat-label>
          <input matInput type="number" formControlName="maxCapacity" required>
          <mat-error *ngIf="eventForm.controls['maxCapacity'].hasError('required')">Capacity is required</mat-error>
          <mat-error *ngIf="eventForm.controls['maxCapacity'].hasError('min')">Capacity must be at least 1</mat-error>
          <mat-error *ngIf="eventForm.controls['maxCapacity'].hasError('max')">Capacity cannot exceed 200</mat-error>
        </mat-form-field>
      </mat-dialog-content>

      <!-- Apply button -->

      <mat-dialog-actions align="end">
        <button mat-button type="button" (click)="onCancel()">Cancel</button>
        <button mat-raised-button color="primary" type="submit" [disabled]="eventForm.invalid">
          {{ isEditMode ? 'Update' : 'Create' }}
        </button>
      </mat-dialog-actions>
    </form>
  `,
  styles: [`
    .full-width {
      width: 100%;
      margin-bottom: 16px;
    }
    mat-dialog-content {
      min-width: 500px;
    }
    .date-time-container {
      display: flex;
      gap: 16px;
      margin-bottom: 16px;
    }
    .date-field {
      flex: 3;
    }
    .time-field {
      flex: 2;
    }
  `]
})
export class EventFormComponent {
  eventForm: FormGroup;
  isEditMode: boolean;
  today = new Date();

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EventFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { event: EventDTO }
  ) {
    this.isEditMode = !!data.event.eventId;

    // Set up date and time variables as before
    let startDate = null;
    let startTime = '12:00';
    let endDate = null;
    let endTime = '13:00';

    if (data.event.startTime) {
      const startDateTime = new Date(data.event.startTime);
      startDate = startDateTime;
      startTime = startDateTime.toTimeString().substring(0, 5);
    }

    if (data.event.endTime) {
      const endDateTime = new Date(data.event.endTime);
      endDate = endDateTime;
      endTime = endDateTime.toTimeString().substring(0, 5);
    }

    this.eventForm = this.fb.group({
      title: [data.event.title || '', [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(100)
      ]],
      description: [data.event.description || '', [
        Validators.maxLength(1000)
      ]],
      startDate: [startDate, {
        validators: [
          Validators.required,
          futureDateValidator()
        ],
        updateOn: 'change'  // Add this for immediate validation
      }],
      startTime: [startTime, Validators.required],
      endDate: [endDate, {
        validators: [
          Validators.required,
          futureDateValidator()
        ],
        updateOn: 'change'  // Add this for immediate validation
      }],
      endTime: [endTime, Validators.required],
      place: [data.event.place || '', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(200)
      ]],
      eventType: [data.event.eventType || '', Validators.required],
      maxCapacity: [data.event.maxCapacity || 10, [
        Validators.required,
        Validators.min(1),
        Validators.max(200)
      ]]
    });
  }

  onSubmit(): void {
    if (this.eventForm.valid) {
      const formValue = this.eventForm.value;

      // Combine date and time for start and end times
      const startDateTime = this.combineDateAndTime(formValue.startDate, formValue.startTime);
      const endDateTime = this.combineDateAndTime(formValue.endDate, formValue.endTime);

      const result = {
        title: formValue.title,
        description: formValue.description,
        startTime: startDateTime.toISOString(),
        endTime: endDateTime.toISOString(),
        place: formValue.place,
        eventType: formValue.eventType,
        maxCapacity: formValue.maxCapacity
      };

      this.dialogRef.close(result);
    }
  }

  combineDateAndTime(date: Date, timeString: string): Date {
    const result = new Date(date);
    const [hours, minutes] = timeString.split(':').map(Number);
    result.setHours(hours, minutes, 0);
    return result;
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
