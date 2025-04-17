import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModal, NgbPagination } from '@ng-bootstrap/ng-bootstrap';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize, takeUntil } from 'rxjs/operators';

import { CommonModule, DatePipe } from '@angular/common';
import { SubscriptionService } from '../../../../service/mic2/subscription.service';
import { ToastService } from '../../../../service/mic2/toast.service';
import { HttpClientModule } from '@angular/common/http';

@Component({
  selector: 'app-subscription-list',
  templateUrl: './subscription-list.component.html',
  styleUrls: ['./subscription-list.component.scss'],
  standalone: true,
  providers: [DatePipe],
  imports: [CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule, NgbPagination]
})
export class SubscriptionListComponent implements OnInit, OnDestroy {
  // Data sources
  allSubscriptions: any[] = []; // Store all subscriptions for client-side filtering
  filteredSubscriptions: any[] = []; // Filtered subscriptions based on search criteria
  subscriptions: any[] = []; // Current page of subscriptions
  totalItems = 0;

  // Pagination
  page = 1;
  pageSize = 10;

  // Sorting
  sortColumn = 'createdAt';
  sortDirection = 'desc';

  // Filtering
  filterForm: FormGroup;
  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'ACTIVE', label: 'Active' },
    { value: 'PENDING', label: 'Pending' },
    { value: 'CANCELED', label: 'Canceled' },
    { value: 'EXPIRED', label: 'Expired' }
  ];

  // UI State
  loading = false;
  error: string | null = null;
  selectedSubscription: any = null;

  // Unsubscribe notifier
  private destroy$ = new Subject<void>();

  constructor(
    private subscriptionService: SubscriptionService,
    private modalService: NgbModal,
    private toastService: ToastService,
    private fb: FormBuilder,
    private datePipe: DatePipe
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      status: [''],
      startDate: [''],
      endDate: ['']
    });
  }

  ngOnInit(): void {
    this.setupFilterListeners();
    this.loadAllSubscriptions(); // Load all subscriptions first
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupFilterListeners(): void {
    // Apply filters immediately when search or status changes
    this.filterForm.get('search')?.valueChanges
      .pipe(debounceTime(400), distinctUntilChanged(), takeUntil(this.destroy$))
      .subscribe(() => {
        this.page = 1;
        this.applyFilters();
      });

    this.filterForm.get('status')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        this.page = 1;
        this.applyFilters();
      });
  }

  /**
   * Load all subscriptions from the server once
   */
// In the SubscriptionListComponent:
  loadAllSubscriptions(): void {
    this.loading = true;
    this.error = null;

    // Use the new getAllSubscriptions method instead
    this.subscriptionService.getAllSubscriptions()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (response) => {
          this.allSubscriptions = response || [];
          console.log(`Loaded ${this.allSubscriptions.length} subscriptions`);
          this.applyFilters(); // Apply initial filters and pagination
        },
        error: (err) => {
          console.error('Error loading subscriptions:', err);
          this.error = 'Failed to load subscriptions. Please try again.';
        }
      });
  }

  /**
   * Apply filters to the allSubscriptions array
   */
  applyFilters(): void {
    const searchTerm = (this.filterForm.get('search')?.value || '').toLowerCase();
    const status = this.filterForm.get('status')?.value;
    const startDate = this.filterForm.get('startDate')?.value ? new Date(this.filterForm.get('startDate')?.value) : null;
    const endDate = this.filterForm.get('endDate')?.value ? new Date(this.filterForm.get('endDate')?.value) : null;

    // Filter the subscriptions based on criteria
    this.filteredSubscriptions = this.allSubscriptions.filter(subscription => {
      // Search term filter
      const matchesSearch = !searchTerm ||
        (subscription.user?.username?.toLowerCase().includes(searchTerm)) ||
        (subscription.user?.email?.toLowerCase().includes(searchTerm)) ||
        (subscription.course?.title?.toLowerCase().includes(searchTerm));

      // Status filter
      const matchesStatus = !status || subscription.status === status;

      // Date range filter
      let matchesDateRange = true;
      if (startDate) {
        const subscriptionDate = new Date(subscription.startDate);
        matchesDateRange = matchesDateRange && subscriptionDate >= startDate;
      }
      if (endDate) {
        const subscriptionDate = new Date(subscription.endDate || subscription.startDate);
        matchesDateRange = matchesDateRange && subscriptionDate <= endDate;
      }

      return matchesSearch && matchesStatus && matchesDateRange;
    });

    // Apply sorting
    this.sortFilteredSubscriptions();

    // Update total count
    this.totalItems = this.filteredSubscriptions.length;

    // Apply pagination
    this.updatePage();
  }

  /**
   * Sort the filtered subscriptions
   */
  sortFilteredSubscriptions(): void {
    this.filteredSubscriptions.sort((a, b) => {
      // Handle nested properties like 'user.username'
      const getNestedValue = (obj: any, path: string) => {
        return path.split('.').reduce((o, key) => (o && o[key] !== undefined) ? o[key] : null, obj);
      };

      let valueA = getNestedValue(a, this.sortColumn);
      let valueB = getNestedValue(b, this.sortColumn);

      // Handle dates
      if (this.sortColumn.includes('Date') || this.sortColumn === 'createdAt' || this.sortColumn === 'updatedAt') {
        valueA = valueA ? new Date(valueA).getTime() : 0;
        valueB = valueB ? new Date(valueB).getTime() : 0;
      }

      // Handle string comparison
      if (typeof valueA === 'string' && typeof valueB === 'string') {
        valueA = valueA.toLowerCase();
        valueB = valueB.toLowerCase();
      }

      if (this.sortDirection === 'asc') {
        return valueA > valueB ? 1 : valueA < valueB ? -1 : 0;
      } else {
        return valueA < valueB ? 1 : valueA > valueB ? -1 : 0;
      }
    });
  }

  /**
   * Update the current page based on pagination settings
   */
  updatePage(): void {
    const startIndex = (this.page - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.subscriptions = this.filteredSubscriptions.slice(startIndex, endIndex);
  }

  applyDateFilter(): void {
    this.page = 1;
    this.applyFilters();
  }

  resetFilters(): void {
    this.filterForm.reset({
      search: '',
      status: '',
      startDate: '',
      endDate: ''
    });
    this.page = 1;
    this.applyFilters();
  }

  onPageChange(page: number): void {
    this.page = page;
    this.updatePage();
  }

  onSort(column: string): void {
    if (this.sortColumn === column) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }
    this.sortFilteredSubscriptions();
    this.updatePage();
  }

  getSortIcon(column: string): string {
    if (this.sortColumn !== column) return 'fa-sort';
    return this.sortDirection === 'asc' ? 'fa-sort-up' : 'fa-sort-down';
  }

  openSubscriptionDetails(content: any, subscription: any): void {
    this.selectedSubscription = subscription;
    this.modalService.open(content, { size: 'lg', centered: true });
  }

  updateSubscriptionStatus(id: number, status: string): void {
    this.subscriptionService
      .updateSubscriptionStatus(id, status)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          this.toastService.show('Subscription updated successfully', { classname: 'bg-success text-light' });

          // Update the subscription in our local arrays
          const updatedSubscription = { ...this.selectedSubscription, status: status };

          // Update in allSubscriptions
          const allIndex = this.allSubscriptions.findIndex(s => s.id === id);
          if (allIndex !== -1) {
            this.allSubscriptions[allIndex] = updatedSubscription;
          }

          // Update in filteredSubscriptions
          const filteredIndex = this.filteredSubscriptions.findIndex(s => s.id === id);
          if (filteredIndex !== -1) {
            this.filteredSubscriptions[filteredIndex] = updatedSubscription;
          }

          // Update in current page subscriptions
          const currentIndex = this.subscriptions.findIndex(s => s.id === id);
          if (currentIndex !== -1) {
            this.subscriptions[currentIndex] = updatedSubscription;
          }

          // Update the selected subscription if it's open in modal
          if (this.selectedSubscription && this.selectedSubscription.id === id) {
            this.selectedSubscription = updatedSubscription;
          }
        },
        error: (err) => {
          console.error('Error updating subscription', err);
          this.toastService.show('Failed to update subscription', { classname: 'bg-danger text-light' });
        }
      });
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'MMM d, yyyy') || '';
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'ACTIVE':
        return 'badge bg-success';
      case 'PENDING':
        return 'badge bg-warning';
      case 'CANCELED':
        return 'badge bg-danger';
      case 'EXPIRED':
        return 'badge bg-secondary';
      default:
        return 'badge bg-info';
    }
  }

  exportToCSV(): void {
    this.subscriptionService
      .exportSubscriptions()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `subscriptions-${new Date().toISOString().slice(0, 10)}.csv`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error exporting subscriptions', err);
          this.toastService.show('Failed to export subscriptions', { classname: 'bg-danger text-light' });
        }
      });
  }

  // Used for pagination display
  protected readonly Math = Math;
}
