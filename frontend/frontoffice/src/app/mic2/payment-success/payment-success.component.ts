import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService, Invoice } from '../../../services/mic2/payment.service';
import { SubscriptionService } from '../../../services/mic2/subscription.service';
import { DatePipe } from '@angular/common';
import { finalize } from 'rxjs/operators';
import { of, catchError } from 'rxjs';

@Component({
  selector: 'app-payment-success',
  templateUrl: './payment-success.component.html',
  styleUrls: ['./payment-success.component.css'],
  providers: [DatePipe]
})
export class PaymentSuccessComponent implements OnInit {
  subscriptionId?: number;
  paymentId?: number;
  paymentDetails: any;
  subscriptionDetails: any;
  invoiceDetails: Invoice | null = null;
  hasInvoice = false;
  paymentType?: string;
  loading = true;
  invoiceLoading = false;
  error: string | null = null;
  invoiceError: string | null = null;
  currentTime: string = '2025-03-04 16:50:03'; // Updated timestamp
  username: string = 'iitsMahdi';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private subscriptionService: SubscriptionService,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    // Get stored user name if available
    const storedUsername = localStorage.getItem('username');
    if (storedUsername) {
      this.username = storedUsername;
    }

    this.route.queryParams.subscribe(params => {
      console.log('Payment Success - Received parameters:', params);

      // Get the payment ID - this should always be available from the payment component
      this.paymentId = params['paymentId'] ? +params['paymentId'] : undefined;

      // Get the payment type if available
      this.paymentType = params['type'];

      // Get invoice generation status if available
      this.hasInvoice = params['invoiceGenerated'] === 'true';

      if (!this.paymentId) {
        this.error = 'Missing payment information. Please contact support if this issue persists.';
        this.loading = false;
        return;
      }

      // Since we have the payment ID, load the payment details first
      this.loadPaymentDetails();
    });
  }

  private loadPaymentDetails(): void {
    if (!this.paymentId) {
      this.error = 'No payment ID provided';
      this.loading = false;
      return;
    }

    console.log(`Loading payment details for payment ID: ${this.paymentId}`);

    this.paymentService.getPaymentById(this.paymentId)
      .pipe(finalize(() => {
        if (!this.subscriptionId) {
          this.loading = false;
        }
      }))
      .subscribe({
        next: (payment) => {
          console.log('Payment details loaded successfully:', payment);
          this.paymentDetails = payment;

          // Extract subscription information from the payment if it exists
          if (payment && payment.subscription && payment.subscription.id) {
            this.subscriptionId = payment.subscription.id;
            console.log(`Extracted subscription ID: ${this.subscriptionId} from payment`);
            this.loadSubscriptionDetails();
          } else {
            console.warn('No subscription information found in payment details');
            this.loading = false;
          }

          // Try to load invoice information if payment is successful
          if (payment.status === 'SUCCESS') {
            this.loadInvoiceDetails();
          }
        },
        error: (error) => {
          console.error('Error loading payment details:', error);
          this.error = 'Failed to load payment details. Please try again later.';
          this.loading = false;
        }
      });
  }

  private loadSubscriptionDetails(): void {
    if (!this.subscriptionId) {
      console.warn('No subscription ID available to load details');
      this.loading = false;
      return;
    }

    console.log(`Loading subscription details for subscription ID: ${this.subscriptionId}`);

    this.subscriptionService.getSubscriptionById(this.subscriptionId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (subscription) => {
          console.log('Subscription details loaded successfully:', subscription);
          this.subscriptionDetails = subscription;
        },
        error: (error) => {
          console.warn('Error loading subscription details:', error);
          // Don't show this as an error message since we still have payment details
          console.warn('Failed to load subscription details, but payment was successful');
        }
      });
  }

  /**
   * Load invoice details without downloading the PDF
   */
  private loadInvoiceDetails(): void {
    if (!this.paymentId) return;

    this.invoiceLoading = true;

    // Use hasInvoice or try to load directly
    if (this.hasInvoice) {
      this.paymentService.getInvoice(this.paymentId)
        .pipe(
          finalize(() => this.invoiceLoading = false),
          catchError(error => {
            console.warn('Error loading invoice:', error);
            this.invoiceError = 'Invoice not available yet. It may be generated shortly.';
            this.hasInvoice = false;
            return of(null);
          })
        )
        .subscribe(invoice => {
          if (invoice) {
            console.log('Invoice details loaded:', invoice);
            this.invoiceDetails = invoice;
            this.hasInvoice = true;
          } else {
            this.hasInvoice = false;
          }
        });
    } else {
      // Check if an invoice exists
      this.paymentService.hasInvoice(this.paymentId)
        .pipe(finalize(() => this.invoiceLoading = false))
        .subscribe(exists => {
          this.hasInvoice = exists;
          if (exists) {
            this.loadInvoiceDetails(); // Recursive call now that we know it exists
          }
        });
    }
  }

  downloadInvoice(): void {
    if (!this.paymentId) {
      this.error = "Payment ID is required to download invoice";
      return;
    }

    if (!this.hasInvoice) {
      this.error = "Invoice is not available yet. Please try again later.";
      return;
    }

    console.log(`Downloading invoice for payment ID: ${this.paymentId}`);
    this.loading = true;

    this.paymentService.downloadInvoice(this.paymentId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          const blob = new Blob([response], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;

          // Use invoice number if available, otherwise use payment ID
          const fileName = this.invoiceDetails?.invoiceNumber
            ? `Invoice_${this.invoiceDetails.invoiceNumber}.pdf`
            : `invoice_${this.paymentId}_${this.formatCurrentDate()}.pdf`;

          a.download = fileName;

          document.body.appendChild(a);
          a.click();
          document.body.removeChild(a);
          window.URL.revokeObjectURL(url);

          console.log('Invoice downloaded successfully');
        },
        error: (error) => {
          console.error('Error downloading invoice:', error);
          this.error = error.message || 'Failed to download invoice. Please try again later.';
        }
      });
  }

  printInvoice(): void {
    if (!this.paymentId) {
      this.error = "Payment ID is required to print invoice";
      return;
    }

    if (!this.hasInvoice) {
      this.error = "Invoice is not available yet. Please try again later.";
      return;
    }

    console.log(`Printing invoice for payment ID: ${this.paymentId}`);
    this.loading = true;

    this.paymentService.downloadInvoice(this.paymentId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          const blob = new Blob([response], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          const printWindow = window.open(url, '_blank');

          if (printWindow) {
            printWindow.onload = () => {
              console.log('Print window loaded, initiating print');
              printWindow.print();
            };
          } else {
            console.error('Print window blocked by browser');
            this.error = 'Pop-up blocked. Please allow pop-ups to print the invoice.';
          }
        },
        error: (error) => {
          console.error('Error printing invoice:', error);
          this.error = error.message || 'Failed to print invoice. Please try again later.';
        }
      });
  }

  goToCourses(): void {
    console.log('Navigating to courses page');
    this.router.navigate(['/courses']);
  }

  goToSubscriptions(): void {
    console.log('Navigating to my subscriptions page');
    this.router.navigate(['/my-subscriptions']);
  }

  getCourseTitle(): string {
    return this.subscriptionDetails?.course?.title ||
      this.paymentDetails?.subscription?.course?.title ||
      'Course';
  }

  getPaymentMethod(): string {
    return this.paymentDetails?.paymentMethod || 'Credit Card';
  }

  getAmount(): string {
    const amount = this.paymentDetails?.amount || 0;
    const currency = this.paymentDetails?.currency || 'USD';
    const currencySymbol = currency === 'EUR' ? '€' : '$';

    return `${currencySymbol}${amount.toFixed(2)}`;
  }

  getPaymentType(): string {
    if (this.paymentType === 'INSTALLMENT' ||
      this.subscriptionDetails?.paymentType === 'INSTALLMENTS' ||
      this.paymentDetails?.subscription?.paymentType === 'INSTALLMENTS') {
      return 'Installment Payment';
    }
    return 'Full Payment';
  }

  getPaymentDate(): string {
    if (!this.paymentDetails?.paymentDate) return 'N/A';

    return this.datePipe.transform(this.paymentDetails.paymentDate, 'medium') || 'N/A';
  }

  getSubscriptionDuration(): string {
    if (!this.subscriptionDetails) return 'N/A';

    const startDate = this.subscriptionDetails.startDate ?
      this.datePipe.transform(this.subscriptionDetails.startDate, 'mediumDate') : 'N/A';

    const endDate = this.subscriptionDetails.endDate ?
      this.datePipe.transform(this.subscriptionDetails.endDate, 'mediumDate') : 'N/A';

    return `${startDate} - ${endDate}`;
  }

  formatCurrentDate(): string {
    return this.datePipe.transform(new Date(), 'yyyy-MM-dd') || '';
  }

  getInstallmentInfo(): string {
    if (this.paymentType !== 'INSTALLMENT' &&
      this.subscriptionDetails?.paymentType !== 'INSTALLMENTS' &&
      this.paymentDetails?.subscription?.paymentType !== 'INSTALLMENTS') {
      return '';
    }

    const current = this.getCurrentInstallmentNumber();
    const total = this.getTotalInstallments();

    if (current && total) {
      return `Installment ${current} of ${total}`;
    }

    return '';
  }

  getCurrentInstallmentNumber(): number {
    // First check if we have it from the invoice
    if (this.invoiceDetails?.installmentNumber) {
      return this.invoiceDetails.installmentNumber;
    }

    // Try to determine from payment details
    if (this.paymentDetails?.paymentSchedules?.length) {
      const paidSchedules = this.paymentDetails.paymentSchedules.filter(
        (schedule: any) => schedule.status === 'PAID'
      );
      return paidSchedules.length || 1;
    }
    return 1;
  }

  getTotalInstallments(): number {
    // First check if we have it from the invoice
    if (this.invoiceDetails?.totalInstallments) {
      return this.invoiceDetails.totalInstallments;
    }

    // Try to determine from payment details
    if (this.paymentDetails?.paymentSchedules?.length) {
      return this.paymentDetails.paymentSchedules.length;
    } else if (this.subscriptionDetails?.installments) {
      return this.subscriptionDetails.installments;
    }
    return 0;
  }

  // Get invoice information for display
  getInvoiceNumber(): string {
    return this.invoiceDetails?.invoiceNumber || `INV-${this.paymentId}`;
  }

  getTaxAmount(): string {
    if (!this.invoiceDetails?.taxAmount) return 'N/A';

    const currency = this.invoiceDetails.currency || 'USD';
    const currencySymbol = currency === 'EUR' ? '€' : '$';

    return `${currencySymbol}${this.invoiceDetails.taxAmount}`;
  }

  getSubtotal(): string {
    if (!this.invoiceDetails?.subtotal) return 'N/A';

    const currency = this.invoiceDetails.currency || 'USD';
    const currencySymbol = currency === 'EUR' ? '€' : '$';

    return `${currencySymbol}${this.invoiceDetails.subtotal}`;
  }

  getInvoiceDate(): string {
    if (!this.invoiceDetails?.issuedDate) return 'N/A';

    return this.datePipe.transform(this.invoiceDetails.issuedDate, 'medium') || 'N/A';
  }
}
