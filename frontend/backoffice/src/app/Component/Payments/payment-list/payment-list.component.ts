import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgbModal, NgbPagination } from '@ng-bootstrap/ng-bootstrap';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize, takeUntil } from 'rxjs/operators';
import { CommonModule, DatePipe } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';

import { ToastService } from '../../../../service/mic2/toast.service';
import { PaymentService } from '../../../../service/mic2/payment.service';

@Component({
  selector: 'app-payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.scss'],
  standalone: true,
  providers: [DatePipe],
  imports: [CommonModule, HttpClientModule, FormsModule, ReactiveFormsModule, NgbPagination]
})
export class PaymentListComponent implements OnInit, OnDestroy {
  // Data sources
  allPayments: any[] = []; // Store all payments for client-side filtering
  filteredPayments: any[] = []; // Filtered payments based on search criteria
  payments: any[] = []; // Current page of payments
  totalItems = 0;

  // Pagination
  page = 1;
  pageSize = 10;

  // Sorting
  sortColumn = 'paymentDate';
  sortDirection = 'desc';

  // Filtering
  filterForm: FormGroup;
  statusOptions = [
    { value: '', label: 'All Statuses' },
    { value: 'SUCCESS', label: 'Success' },
    { value: 'PENDING', label: 'Pending' },
    { value: 'FAILED', label: 'Failed' },
    { value: 'REFUNDED', label: 'Refunded' }
  ];

  paymentMethodOptions = [
    { value: '', label: 'All Methods' },
    { value: 'CREDIT_CARD', label: 'Credit Card' },
    { value: 'PAYPAL', label: 'PayPal' },
    { value: 'STRIPE', label: 'Stripe' },
    { value: 'BANK_TRANSFER', label: 'Bank Transfer' }
  ];

  // UI State
  loading = false;
  error: string | null = null;
  selectedPayment: any = null;

  // Stats
  paymentStats = {
    total: 0,
    success: 0,
    pending: 0,
    failed: 0,
    totalAmount: 0
  };

  // Unsubscribe notifier
  private destroy$ = new Subject<void>();

  constructor(
    private paymentService: PaymentService,
    private modalService: NgbModal,
    private toastService: ToastService,
    private fb: FormBuilder,
    private datePipe: DatePipe
  ) {
    this.filterForm = this.fb.group({
      search: [''],
      status: [''],
      paymentMethod: [''],
      startDate: [''],
      endDate: [''],
      minAmount: [''],
      maxAmount: ['']
    });
  }

  ngOnInit(): void {
    this.setupFilterListeners();
    this.loadAllPayments();
    this.loadPaymentStats();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private setupFilterListeners(): void {
    // Apply filters immediately when search, status or payment method changes
    this.filterForm.get('search')?.valueChanges.pipe(
      debounceTime(400),
      distinctUntilChanged(),
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.page = 1;
      this.applyFilters();
    });

    this.filterForm.get('status')?.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.page = 1;
      this.applyFilters();
    });

    this.filterForm.get('paymentMethod')?.valueChanges.pipe(
      takeUntil(this.destroy$)
    ).subscribe(() => {
      this.page = 1;
      this.applyFilters();
    });
  }

  /**
   * Load all payments from the server once
   */
  loadAllPayments(): void {
    this.loading = true;
    this.error = null;

    this.paymentService.getAllPayments()
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => this.loading = false)
      )
      .subscribe({
        next: (response) => {
          console.log(response)
          this.allPayments = response || [];
          console.log(`Loaded ${this.allPayments.length} payments`);
          this.applyFilters(); // Apply initial filters and pagination
        },
        error: (err) => {
          console.error('Error loading payments:', err);
          this.error = 'Failed to load payments. Please try again.';
        }
      });
  }

  /**
   * Load payment statistics
   */
  loadPaymentStats(): void {
    this.paymentService.getPaymentStats()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.paymentStats = stats;
        },
        error: (err) => {
          console.error('Error loading payment stats', err);
        }
      });
  }

  /**
   * Apply filters to the allPayments array
   */
  applyFilters(): void {
    const searchTerm = (this.filterForm.get('search')?.value || '').toLowerCase();
    const status = this.filterForm.get('status')?.value;
    const paymentMethod = this.filterForm.get('paymentMethod')?.value;
    const startDate = this.filterForm.get('startDate')?.value ? new Date(this.filterForm.get('startDate')?.value) : null;
    const endDate = this.filterForm.get('endDate')?.value ? new Date(this.filterForm.get('endDate')?.value) : null;
    const minAmount = parseFloat(this.filterForm.get('minAmount')?.value) || 0;
    const maxAmount = parseFloat(this.filterForm.get('maxAmount')?.value) || Number.MAX_SAFE_INTEGER;

    // Filter the payments based on criteria
    this.filteredPayments = this.allPayments.filter(payment => {
      // Search term filter (check user, reference number, course)
      const matchesSearch = !searchTerm ||
        (payment.user?.username?.toLowerCase().includes(searchTerm)) ||
        (payment.user?.email?.toLowerCase().includes(searchTerm)) ||
        (payment.referenceNumber?.toLowerCase().includes(searchTerm)) ||
        (payment.course?.title?.toLowerCase().includes(searchTerm));

      // Status filter
      const matchesStatus = !status || payment.status === status;

      // Payment method filter
      const matchesPaymentMethod = !paymentMethod || payment.paymentMethod === paymentMethod;

      // Date range filter
      let matchesDateRange = true;
      if (startDate) {
        const paymentDate = new Date(payment.paymentDate);
        matchesDateRange = matchesDateRange && paymentDate >= startDate;
      }
      if (endDate) {
        const paymentDate = new Date(payment.paymentDate);
        matchesDateRange = matchesDateRange && paymentDate <= endDate;
      }

      // Amount range filter
      const amount = parseFloat(payment.amount) || 0;
      const matchesAmountRange = amount >= minAmount && amount <= maxAmount;

      return matchesSearch && matchesStatus && matchesPaymentMethod &&
        matchesDateRange && matchesAmountRange;
    });

    // Apply sorting
    this.sortFilteredPayments();

    // Update total count
    this.totalItems = this.filteredPayments.length;

    // Apply pagination
    this.updatePage();
  }

  /**
   * Sort the filtered payments
   */
  sortFilteredPayments(): void {
    this.filteredPayments.sort((a, b) => {
      // Handle nested properties like 'user.username'
      const getNestedValue = (obj: any, path: string) => {
        return path.split('.').reduce((o, key) => (o && o[key] !== undefined) ? o[key] : null, obj);
      };

      let valueA = getNestedValue(a, this.sortColumn);
      let valueB = getNestedValue(b, this.sortColumn);

      // Handle dates
      if (this.sortColumn === 'paymentDate' || this.sortColumn === 'createdAt' || this.sortColumn === 'updatedAt') {
        valueA = valueA ? new Date(valueA).getTime() : 0;
        valueB = valueB ? new Date(valueB).getTime() : 0;
      }

      // Handle numbers like amount
      if (this.sortColumn === 'amount') {
        valueA = parseFloat(valueA) || 0;
        valueB = parseFloat(valueB) || 0;
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
    this.payments = this.filteredPayments.slice(startIndex, endIndex);
  }

  applyAdvancedFilters(): void {
    this.page = 1;
    this.applyFilters();
  }

  resetFilters(): void {
    this.filterForm.reset({
      search: '',
      status: '',
      paymentMethod: '',
      startDate: '',
      endDate: '',
      minAmount: '',
      maxAmount: ''
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
    this.sortFilteredPayments();
    this.updatePage();
  }

  getSortIcon(column: string): string {
    if (this.sortColumn !== column) return 'fa-sort';
    return this.sortDirection === 'asc' ? 'fa-sort-up' : 'fa-sort-down';
  }

  openPaymentDetails(content: any, payment: any): void {
    this.selectedPayment = payment;
    this.modalService.open(content, { size: 'lg', centered: true });
  }

  updatePaymentStatus(id: number, status: string): void {
    this.paymentService.updatePaymentStatus(id, status)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastService.show('Payment status updated successfully', { classname: 'bg-success text-light' });

          // Update the payment in local arrays
          const updatedPayment = { ...this.findPaymentById(id), status: status };
          this.updatePaymentInArrays(id, updatedPayment);

          // Refresh payment stats after status change
          this.loadPaymentStats();
        },
        error: (err) => {
          console.error('Error updating payment status', err);
          this.toastService.show('Failed to update payment status', { classname: 'bg-danger text-light' });
        }
      });
  }

  refundPayment(id: number): void {
    this.paymentService.refundPayment(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastService.show('Payment refunded successfully', { classname: 'bg-success text-light' });

          // Update the payment status in local arrays
          const updatedPayment = { ...this.findPaymentById(id), status: 'REFUNDED' };
          this.updatePaymentInArrays(id, updatedPayment);

          // Refresh payment stats after refund
          this.loadPaymentStats();
        },
        error: (err) => {
          console.error('Error refunding payment', err);
          this.toastService.show('Failed to refund payment', { classname: 'bg-danger text-light' });
        }
      });
  }

  sendPaymentReminder(id: number): void {
    this.paymentService.sendPaymentReminder(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.toastService.show('Payment reminder sent successfully', { classname: 'bg-success text-light' });
        },
        error: (err) => {
          console.error('Error sending payment reminder', err);
          this.toastService.show('Failed to send payment reminder', { classname: 'bg-danger text-light' });
        }
      });
  }

  generateReceipt(id: number): void {
    this.paymentService.generateReceipt(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `payment-receipt-${id}.pdf`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error generating receipt', err);
          this.toastService.show('Failed to generate receipt', { classname: 'bg-danger text-light' });
        }
      });
  }

  // Helper method to find payment by ID
  private findPaymentById(id: number): any {
    return this.allPayments.find(payment => payment.id === id) || {};
  }

  // Helper method to update payment in all arrays
  private updatePaymentInArrays(id: number, updatedPayment: any): void {
    // Update in allPayments
    const allIndex = this.allPayments.findIndex(p => p.id === id);
    if (allIndex !== -1) {
      this.allPayments[allIndex] = updatedPayment;
    }

    // Update in filteredPayments
    const filteredIndex = this.filteredPayments.findIndex(p => p.id === id);
    if (filteredIndex !== -1) {
      this.filteredPayments[filteredIndex] = updatedPayment;
    }

    // Update in current page payments
    const currentIndex = this.payments.findIndex(p => p.id === id);
    if (currentIndex !== -1) {
      this.payments[currentIndex] = updatedPayment;
    }

    // Update the selected payment if it's open in modal
    if (this.selectedPayment && this.selectedPayment.id === id) {
      this.selectedPayment = updatedPayment;
    }
  }

  formatDate(date: string): string {
    return this.datePipe.transform(date, 'MMM d, yyyy, h:mm a') || '';
  }

  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);
  }

  getStatusBadgeClass(status: string): string {
    switch (status) {
      case 'SUCCESS': return 'badge bg-success';
      case 'PENDING': return 'badge bg-warning';
      case 'FAILED': return 'badge bg-danger';
      case 'REFUNDED': return 'badge bg-secondary';
      default: return 'badge bg-info';
    }
  }

  exportToCSV(): void {
    this.paymentService.exportPayments()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `payments-${new Date().toISOString().slice(0,10)}.csv`;
          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);
        },
        error: (err) => {
          console.error('Error exporting payments', err);
          this.toastService.show('Failed to export payments', { classname: 'bg-danger text-light' });
        }
      });
  }

  // Used for pagination display
  protected readonly Math = Math;
}
