// src/app/Component/event-form/event-form.component.ts
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { EventDTO } from '../../../model/mic5/event-dto';

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
  template: `
    <h2 mat-dialog-title>{{ isEditMode ? 'Edit Event' : 'Create New Event' }}</h2>
    <form [formGroup]="eventForm" (ngSubmit)="onSubmit()">
      <mat-dialog-content>
        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Title</mat-label>
          <input matInput formControlName="title" required>
          <mat-error *ngIf="eventForm.controls['title'].hasError('required')">Title is required</mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Description</mat-label>
          <textarea matInput formControlName="description" rows="4" required></textarea>
          <mat-error *ngIf="eventForm.controls['description'].hasError('required')">Description is required</mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Date & Time</mat-label>
          <input matInput [matDatepicker]="picker" formControlName="startTime" required>
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
          <mat-error *ngIf="eventForm.controls['startTime'].hasError('required')">Date is required</mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Place</mat-label>
          <input matInput formControlName="place" required>
          <mat-error *ngIf="eventForm.controls['place'].hasError('required')">Place is required</mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Event Type</mat-label>
          <mat-select formControlName="eventType" required>
            <mat-option value="CONFERENCE">Conference</mat-option>
            <mat-option value="WORKSHOP">Workshop</mat-option>
            <mat-option value="SEMINAR">Seminar</mat-option>
            <mat-option value="OTHER">Other</mat-option>
          </mat-select>
          <mat-error *ngIf="eventForm.controls['eventType'].hasError('required')">Event type is required</mat-error>
        </mat-form-field>

        <mat-form-field appearance="fill" class="full-width">
          <mat-label>Maximum Capacity</mat-label>
          <input matInput type="number" formControlName="maxCapacity" required>
          <mat-error *ngIf="eventForm.controls['maxCapacity'].hasError('required')">Capacity is required</mat-error>
          <mat-error *ngIf="eventForm.controls['maxCapacity'].hasError('min')">Capacity must be at least 1</mat-error>
        </mat-form-field>
      </mat-dialog-content>

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
  `]
})
export class EventFormComponent {
  eventForm: FormGroup;
  isEditMode: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<EventFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { event: EventDTO }
  ) {
    this.isEditMode = !!data.event.eventId;
    this.eventForm = this.fb.group({
      title: [data.event.title || '', Validators.required],
      description: [data.event.description || '', Validators.required],
      startTime: [data.event.startTime ? new Date(data.event.startTime) : null, Validators.required],
      place: [data.event.place || '', Validators.required],
      eventType: [data.event.eventType || '', Validators.required],
      maxCapacity: [data.event.maxCapacity || 10, [Validators.required, Validators.min(1)]]
    });
  }

  onSubmit(): void {
    if (this.eventForm.valid) {
      const formData = this.eventForm.value;
      this.dialogRef.close(formData);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
