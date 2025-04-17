import { Component, OnInit } from '@angular/core';
import { PaymentService, PaymentScheduleDTO } from '../../../services/mic2/payment.service';
import { SubscriptionService } from '../../../services/mic2/subscription.service';
import { finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

// Define interface for grouped schedules structure
interface GroupedSchedule {
  subscriptionId: number;
  courseName: string;
  schedules: PaymentScheduleDTO[];
}

@Component({
  selector: 'app-payment-hist',
  templateUrl: './payment-hist.component.html',
  styleUrls: ['./payment-hist.component.css']
})
export class PaymentHistComponent implements OnInit {
  loading = false;
  error: string | null = null;
  unpaidSchedules: PaymentScheduleDTO[] = [];
  groupedSchedules: Record<string, GroupedSchedule> = {};
  currentUser: string="";
  userId: number | null = null;
  currentDate:Date = new Date();

  constructor(
    private paymentService: PaymentService,
    private subscriptionService: SubscriptionService,
    public router: Router
  ) {}

  ngOnInit(): void {
    this.currentUser =<string>localStorage.getItem('username');

    this.getUserId();
  }

  private getUserId(): void {
    this.loading = true;
    this.subscriptionService.getUserByUsername(this.currentUser)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          console.log('User response:', response);

          if (!response) {
            this.error = `User '${this.currentUser}' not found`;
            this.showErrorAlert(this.error);
            return;
          }

          if (!response.id) {
            console.error('Response missing ID property:', response);
            this.error = 'User data is incomplete';
            this.showErrorAlert(this.error);
            return;
          }

          this.userId = response.id;
          this.loadUnpaidSchedules();
        },
        error: (error) => {
          console.error('Error fetching user:', error);
          this.error = `Failed to retrieve user information: ${error.message || 'Unknown error'}`;
          this.showErrorAlert(this.error);
        }
      });
  }

  private loadUnpaidSchedules(): void {
    if (!this.userId) {
      this.error = "User ID not available";
      return;
    }

    this.loading = true;
    this.error = null;

    this.paymentService.getUserUnpaidSchedules(this.userId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (schedules) => {
          console.log('Received unpaid schedules:', schedules);

          if (!schedules) {
            this.error = "Invalid response from server";
            return;
          }

          this.unpaidSchedules = schedules;
          this.groupSchedulesBySubscription();

          if (schedules.length === 0) {
            this.error = "No unpaid schedules found.";
          }
        },
        error: (error) => {
          console.error('Error loading schedules:', error);
          this.error = `Failed to load unpaid schedules: ${error.message || 'Unknown error'}`;
          this.showErrorAlert(this.error);
        }
      });
  }

  private groupSchedulesBySubscription(): void {
    console.log('Grouping schedules by subscription');
    this.groupedSchedules = {};

    if (!this.unpaidSchedules || this.unpaidSchedules.length === 0) {
      console.log('No schedules to group');
      return;
    }

    this.unpaidSchedules.forEach(schedule => {
      console.log('Processing schedule:', schedule);

      if (!schedule || !schedule.subscriptionId) {
        console.warn('Schedule missing required data:', schedule);
        return;
      }

      const subscriptionId = schedule.subscriptionId;
      const courseName = schedule.courseName || 'Unknown Course';

      const key = `${subscriptionId}-${courseName}`;

      if (!this.groupedSchedules[key]) {
        this.groupedSchedules[key] = {
          subscriptionId,
          courseName,
          schedules: []
        };
      }

      this.groupedSchedules[key].schedules.push(schedule);
    });

    // Sort schedules within each group by due date
    Object.values(this.groupedSchedules).forEach(group => {
      group.schedules.sort((a, b) => {
        return new Date(a.dueDate).getTime() - new Date(b.dueDate).getTime();
      });
    });
  }

  navigateToPayment(schedule: PaymentScheduleDTO): void {
    if (!schedule.subscriptionId) {
      this.showErrorAlert('Cannot process this payment: Missing subscription information.');
      return;
    }

    // Navigate to payment page with appropriate query parameters
    this.router.navigate(['/payment'], {
      queryParams: {
        subscriptionId: schedule.subscriptionId,
        scheduleId: schedule.id,
        installmentNumber: schedule.installmentNumber,
        amount: schedule.amount,
        paymentType: 'INSTALLMENTS'
      }
    });
  }

  isScheduleOverdue(schedule: PaymentScheduleDTO): boolean {
    if (!schedule?.dueDate) return false;

    const dueDate = new Date(schedule.dueDate);
    return dueDate < this.currentDate && schedule.status === 'PENDING';
  }

  getStatusClass(schedule: PaymentScheduleDTO): string {
    if (schedule.status === 'OVERDUE') return 'bg-red-100 text-red-800';
    if (this.isScheduleOverdue(schedule)) return 'bg-orange-100 text-orange-800';
    return 'bg-yellow-100 text-yellow-800';
  }

  getDaysOverdue(schedule: PaymentScheduleDTO): number {
    if (!schedule?.dueDate) return 0;

    const dueDate = new Date(schedule.dueDate);
    if (dueDate >= this.currentDate) return 0;

    const diffTime = Math.abs(this.currentDate.getTime() - dueDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  // New methods for the summary section
  getTotalAmountDue(): number {
    return this.unpaidSchedules.reduce((sum, schedule) => sum + schedule.amount, 0);
  }

  getNextDueDate(): Date | null {
    if (this.unpaidSchedules.length === 0) return null;

    // Find the earliest due date
    return this.unpaidSchedules
      .map(s => new Date(s.dueDate))
      .sort((a, b) => a.getTime() - b.getTime())[0];
  }

  getOverdueCount(): number {
    return this.unpaidSchedules.filter(schedule => this.isScheduleOverdue(schedule)).length;
  }

  private showErrorAlert(message: string): void {
    Swal.fire({
      title: 'Error',
      text: message,
      icon: 'error',
      confirmButtonText: 'OK'
    });
  }
  getCardStatusClass(schedule: PaymentScheduleDTO): string {
    if (schedule.status === 'OVERDUE') return 'overdue';
    if (this.isScheduleOverdue(schedule)) return 'late';
    return 'pending';
  }
}
