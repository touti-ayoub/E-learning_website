import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../../services/mic2/payment.service';
import { SubscriptionService } from '../../../services/mic2/subscription.service';
import { DatePipe } from '@angular/common';

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
  loading = true;
  error: string | null = null;
  currentTime: string = '2025-03-02 23:02:52';
  username: string = 'iitsMahdi';

  // Flag to check if we're using test data
  isTestMode = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private paymentService: PaymentService,
    private subscriptionService: SubscriptionService,
    private datePipe: DatePipe
  ) { }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      console.warn('Received URL params:', params);

      // First try to get from query params
      this.paymentId = params['paymentId'] ? Number(params['paymentId']) : undefined;
      this.subscriptionId = params['subscriptionId'] ? Number(params['subscriptionId']) : undefined;

      // Alternative: try different parameter naming
      if (!this.paymentId) {
        this.paymentId = params['payment_id'] ? Number(params['payment_id']) : undefined;
      }

      if (!this.subscriptionId) {
        this.subscriptionId = params['subscription_id'] ? Number(params['subscription_id']) : undefined;
      }

      // Check query param 'type' which might indicate which flow we're in
      const type = params['type'];

      // If parameters are still missing, check if we can handle the situation
      if (!this.paymentId || !this.subscriptionId) {
        // For demo/testing purpose: use mock data if no IDs are provided
        if (this.isInDevelopment() || type === 'demo') {
          this.isTestMode = true;
          this.paymentId = 12345;
          this.subscriptionId = 67890;
          console.warn('Using mock data for testing:', {
            paymentId: this.paymentId,
            subscriptionId: this.subscriptionId
          });
        } else {
          // In production, try to recover with just one ID
          if (this.paymentId && !this.subscriptionId) {
            // We have payment ID but no subscription ID
            // We can still load payment details and attempt to get subscription from it
            this.loadPaymentDetailsOnly();
            return;
          } else if (!this.paymentId && this.subscriptionId) {
            // We have subscription ID but no payment ID
            // We can try to load subscription and get latest payment from it
            this.loadSubscriptionDetailsOnly();
            return;
          } else {
            // We have neither - show error
            this.error = 'Missing payment and subscription information';
            this.loading = false;
            return;
          }
        }
      }

      this.loadPaymentAndSubscriptionDetails();
    });
  }

  private isInDevelopment(): boolean {
    // Check if we're running in development mode
    // This is a simplified check - you may need to adjust based on your environment setup
    return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
  }

  private loadPaymentDetailsOnly(): void {
    if (!this.paymentId) return;

    this.paymentService.getPaymentById(this.paymentId).subscribe({
      next: (payment) => {
        console.log('Payment details loaded:', payment);
        this.paymentDetails = payment;

        // If payment has subscription info, try to get that as well
        if (payment.subscription && payment.subscription.id) {
          this.subscriptionId = payment.subscription.id;
          this.loadSubscriptionDetails();
        } else {
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error loading payment details:', error);
        this.error = 'Failed to load payment details';
        this.loading = false;
      }
    });
  }

  private loadSubscriptionDetailsOnly(): void {
    if (!this.subscriptionId) return;

    this.subscriptionService.getSubscriptionById(this.subscriptionId).subscribe({
      next: (subscription) => {
        console.log('Subscription details loaded:', subscription);
        this.subscriptionDetails = subscription;

        // If subscription has payment info, try to get the latest payment
        if (subscription.payments && subscription.payments.length > 0) {
          // Find the latest payment
          const latestPayment = subscription.payments.reduce((latest:any, current:any) => {
            if (!latest) return current;
            return new Date(current.paymentDate) > new Date(latest.paymentDate) ? current : latest;
          }, null);

          if (latestPayment) {
            this.paymentId = latestPayment.id;
            this.paymentDetails = latestPayment;
          }
        }
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading subscription details:', error);
        this.error = 'Failed to load subscription details';
        this.loading = false;
      }
    });
  }

  private loadPaymentAndSubscriptionDetails(): void {
    // If in test mode, use mock data
    if (this.isTestMode) {
      this.provideMockData();
      return;
    }

    // Load payment details
    this.paymentService.getPaymentById(this.paymentId!).subscribe({
      next: (payment) => {
        console.log('Payment details loaded:', payment);
        this.paymentDetails = payment;
        // Load subscription details after payment is loaded
        this.loadSubscriptionDetails();
      },
      error: (error) => {
        console.error('Error loading payment details:', error);
        this.error = 'Failed to load payment details';
        this.loading = false;
      }
    });
  }

  private loadSubscriptionDetails(): void {
    if (!this.subscriptionId) {
      this.loading = false;
      return;
    }

    this.subscriptionService.getSubscriptionById(this.subscriptionId).subscribe({
      next: (subscription) => {
        console.log('Subscription details loaded:', subscription);
        this.subscriptionDetails = subscription;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading subscription details:', error);
        this.error = 'Failed to load subscription details, but payment was successful';
        this.loading = false;
      }
    });
  }

  private provideMockData(): void {
    // Mock payment data for testing
    this.paymentDetails = {
      id: this.paymentId,
      amount: 150,
      currency: 'USD',
      paymentMethod: 'Credit Card',
      paymentDate: new Date().toISOString(),
      status: 'PAID'
    };

    // Mock subscription data
    this.subscriptionDetails = {
      id: this.subscriptionId,
      startDate: new Date().toISOString(),
      endDate: new Date(new Date().setMonth(new Date().getMonth() + 6)).toISOString(),
      status: 'ACTIVE',
      paymentType: 'FULL',
      autoRenew: false,
      course: {
        id: 101,
        title: 'Advanced Web Development',
        instructor: 'John Smith'
      }
    };

    // Simulate network delay
    setTimeout(() => {
      this.loading = false;
    }, 1000);
  }

  downloadInvoice(): void {
    if (!this.paymentId) {
      this.error = "Payment ID is required to download invoice";
      return;
    }

    if (this.isTestMode) {
      alert('This is a test mode. In a real environment, this would download an invoice PDF.');
      return;
    }

    this.paymentService.downloadInvoice(this.paymentId).subscribe({
      next: (response) => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `invoice_${this.paymentId}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Error downloading invoice:', error);
        this.error = 'Failed to download invoice';
      }
    });
  }

  printInvoice(): void {
    if (!this.paymentId) {
      this.error = "Payment ID is required to print invoice";
      return;
    }

    if (this.isTestMode) {
      alert('This is a test mode. In a real environment, this would open a printable invoice PDF.');
      return;
    }

    this.paymentService.downloadInvoice(this.paymentId).subscribe({
      next: (response) => {
        const blob = new Blob([response], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const printWindow = window.open(url, '_blank');
        if (printWindow) {
          printWindow.onload = () => {
            printWindow.print();
          };
        } else {
          this.error = 'Pop-up blocked. Please allow pop-ups to print the invoice.';
        }
      },
      error: (error) => {
        console.error('Error printing invoice:', error);
        this.error = 'Failed to print invoice';
      }
    });
  }

  goToCourses(): void {
    this.router.navigate(['/courses']);
  }

  goToSubscriptions(): void {
    this.router.navigate(['/my-subscriptions']);
  }
}
