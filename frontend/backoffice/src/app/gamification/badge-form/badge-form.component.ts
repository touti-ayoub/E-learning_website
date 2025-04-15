import { Component, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-badge-form',
  templateUrl: './badge-form.component.html',
  styleUrls: ['./badge-form.component.scss']
})
export class BadgeFormComponent {
  badgeForm: FormGroup;
  isEdit: boolean;
  currentDate = '2025-03-05 01:01:32';
  currentUser = 'nessimayadi12';

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<BadgeFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.isEdit = !!data.badge?.idBadge;
    this.initForm();
  }

  private initForm(): void {
    this.badgeForm = this.fb.group({
      name: [this.data.badge?.name || '', [Validators.required]],
      description: [this.data.badge?.description || '', [Validators.required]],
      iconUrl: [this.data.badge?.iconUrl || '', [Validators.required]],
      createdAt: [this.currentDate],
      createdBy: [this.currentUser]
    });
  }

  onSubmit(): void {
    if (this.badgeForm.valid) {
      this.dialogRef.close(this.badgeForm.value);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}