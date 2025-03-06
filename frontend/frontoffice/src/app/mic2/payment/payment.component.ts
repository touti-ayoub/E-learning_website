import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../../services/mic2/payment.service';
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';

interface PaymentResponse {
  id: number;
  amount: number;
  currency: string;
  status: string;
  paymentMethod?: string;
  paymentDate?: string;
  dueDate?: string;
  subscription?: any;
  paymentSchedules?: any[];
}

@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css']
})
export class PaymentComponent implements OnInit {
  // System info
  currentUser = '';
  currentDate = new Date('2025-03-02T22:39:59');
  // Component state
  loading = false;
  processing = false;
  error: string | null = null;
  success = false;

  // Payment data
  paymentId?: number;
  payment?: PaymentResponse;
  subscriptionId?: number;
  scheduleId?: number;
  installmentNumber?: number;
  amount?: number;
  paymentType = 'FULL';  // Default to FULL payment

  // Form data & validation
  cardDetails = {
    number: '',
    holder: '',
    expiry: '',
    cvv: ''
  };

  cardNumberInvalid = false;
  cardHolderInvalid = false;
  expiryInvalid = false;
  cvvInvalid = false;

  cardNumberTouched = false;
  cardHolderTouched = false;
  expiryTouched = false;
  cvvTouched = false;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.currentUser =<string>localStorage.getItem('username');

    this.getQueryParams();
    if (this.subscriptionId) {
      this.loadPaymentDetails();
    } else if (this.paymentId) {
      this.loadExistingPayment();
    } else {
      this.error = 'Missing required payment information';
    }
  }

  private getQueryParams(): void {
    this.route.queryParams.subscribe(params => {
      this.paymentId = params['paymentId'] ? +params['paymentId'] : undefined;
      this.subscriptionId = params['subscriptionId'] ? +params['subscriptionId'] : undefined;
      this.scheduleId = params['scheduleId'] ? +params['scheduleId'] : undefined;
      this.installmentNumber = params['installmentNumber'] ? +params['installmentNumber'] : undefined;
      this.amount = params['amount'] ? +params['amount'] : undefined;
      this.paymentType = params['paymentType'] || 'FULL';

      console.log('Query params:', {
        paymentId: this.paymentId,
        subscriptionId: this.subscriptionId,
        scheduleId: this.scheduleId,
        installmentNumber: this.installmentNumber,
        amount: this.amount,
        paymentType: this.paymentType
      });
    });
  }

  private loadPaymentDetails(): void {
    if (!this.subscriptionId) return;

    this.loading = true;
    this.paymentService.initializePayment(this.subscriptionId, this.paymentType)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          this.payment = response;
          this.paymentId = response.id;
          console.log('Payment created:', this.payment);
        },
        error: (error) => {
          this.error = `Failed to load payment details: ${error.message}`;
          console.error('Error creating payment:', error);
        }
      });
  }

  private loadExistingPayment(): void {
    if (!this.paymentId) return;

    this.loading = true;
    this.paymentService.getPaymentById(this.paymentId)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          this.payment = response;
          console.log('Existing payment loaded:', this.payment);
        },
        error: (error) => {
          this.error = `Failed to load payment: ${error.message}`;
          console.error('Error loading payment:', error);
        }
      });
  }

  onSubmit(): void {
    this.markAllFieldsAsTouched();

    if (!this.validatePaymentForm()) return;

    if (this.paymentType === 'INSTALLMENTS') {
      this.processInstallmentPayment();
    } else {
      this.processFullPayment();
    }
  }

  markAllFieldsAsTouched(): void {
    this.cardNumberTouched = true;
    this.cardHolderTouched = true;
    this.expiryTouched = true;
    this.cvvTouched = true;

    // Validate each field
    this.validateCardNumber();
    this.validateCardHolder();
    this.validateExpiry();
    this.validateCVV();
  }

  validateCardNumber(): void {
    const cardNumber = this.cardDetails.number.replace(/\s/g, '');
    this.cardNumberInvalid = !cardNumber || cardNumber.length !== 16 || !/^\d+$/.test(cardNumber);
  }

  validateCardHolder(): void {
    this.cardHolderInvalid = !this.cardDetails.holder || this.cardDetails.holder.trim().length < 3;
  }

  validateExpiry(): void {
    const regex = /^(0[1-9]|1[0-2])\/([0-9]{2})$/;
    this.expiryInvalid = !regex.test(this.cardDetails.expiry);

    if (!this.expiryInvalid) {
      // Check if date is valid and not expired
      const [month, year] = this.cardDetails.expiry.split('/').map(Number);
      const currentYear = new Date().getFullYear() % 100;
      const currentMonth = new Date().getMonth() + 1;

      if (year < currentYear || (year === currentYear && month < currentMonth)) {
        this.expiryInvalid = true;
      }
    }
  }

  validateCVV(): void {
    const regex = /^[0-9]{3,4}$/;
    this.cvvInvalid = !regex.test(this.cardDetails.cvv);
  }

  private processInstallmentPayment(): void {
    if (!this.paymentId || !this.payment) {
      this.error = 'Payment information not available';
      return;
    }

    this.processing = true;
    this.error = null;

    // Process the payment by marking the latest unpaid schedule as paid
    this.paymentService.updatePaymentStatuss(this.paymentId)
      .pipe(finalize(() => this.processing = false))
      .subscribe({
        next: (response) => {
          console.log('Installment payment processed:', response);
          this.success = true;
          this.showSuccessAlert('Payment successful!');

          // Redirect to success page or subscription details
          setTimeout(() => {
            this.router.navigate(['/payment-success'], {
              queryParams: {
                paymentId: this.paymentId,
                type: 'INSTALLMENT'
              }
            });
          }, 1500);
        },
        error: (error) => {
          this.error = `Payment failed: ${error.message}`;
          this.showErrorAlert();
          console.error('Payment error:', error);
        }
      });
  }

  private processFullPayment(): void {
    if (!this.paymentId || !this.payment) {
      this.error = 'Payment information not available';
      return;
    }

    this.processing = true;
    this.error = null;

    // Process the full payment
    this.paymentService.updatePaymentStatuss(this.paymentId)
      .pipe(finalize(() => this.processing = false))
      .subscribe({
        next: (response) => {
          console.log('Full payment processed:', response);
          this.success = true;
          this.showSuccessAlert('Payment successful!');

          // Redirect to success page
          setTimeout(() => {
            this.router.navigate(['/payment-success'], {
              queryParams: {
                paymentId: this.paymentId,
                type: 'FULL'
              }
            });
          }, 1500);
        },
        error: (error) => {
          this.error = `Payment failed: ${error.message}`;
          this.showErrorAlert();
          console.error('Payment error:', error);
        }
      });
  }

  validatePaymentForm(): boolean {
    this.validateCardNumber();
    this.validateCardHolder();
    this.validateExpiry();
    this.validateCVV();

    return !(this.cardNumberInvalid || this.cardHolderInvalid || this.expiryInvalid || this.cvvInvalid);
  }

  formatCardNumber(event: any): void {
    let cardNumber = event.target.value.replace(/\D/g, '');
    cardNumber = cardNumber.substring(0, 16);

    // Add spaces every 4 digits
    const formattedNumber = cardNumber.replace(/(\d{4})(?=\d)/g, '$1 ');
    this.cardDetails.number = formattedNumber;
    this.cardNumberTouched = true;
    this.validateCardNumber();
  }

  formatExpiryDate(event: any): void {
    let expiry = event.target.value.replace(/\D/g, '');

    if (expiry.length > 2) {
      expiry = expiry.substring(0, 2) + '/' + expiry.substring(2, 4);
    }

    this.cardDetails.expiry = expiry;
    this.expiryTouched = true;
    this.validateExpiry();
  }

  restrictCVVInput(event: any): void {
    const cvv = event.target.value.replace(/\D/g, '');
    this.cardDetails.cvv = cvv.substring(0, 4);
    this.cvvTouched = true;
    this.validateCVV();
  }

  showSuccessAlert(message: string): void {
    Swal.fire({
      title: 'Success',
      text: message,
      icon: 'success',
      confirmButtonText: 'OK'
    });
  }

  showErrorAlert(): void {
    Swal.fire({
      title: 'Payment Failed',
      text: this.error || 'An error occurred processing your payment.',
      icon: 'error',
      confirmButtonText: 'Try Again'
    });
  }

  getDisplayAmount(): string {
    const currencySymbol = this.payment?.currency === 'EUR' ? '€' : '$';

    if (this.scheduleId && this.amount) {
      // Show the specific installment amount
      return `${currencySymbol}${this.amount.toFixed(2)}`;
    } else if (this.payment?.amount) {
      // Show the total payment amount
      return `${currencySymbol}${this.payment.amount.toFixed(2)}`;
    }

    return `${currencySymbol}0.00`;
  }

  getPaymentDescription(): string {
    if (this.paymentType === 'INSTALLMENTS') {
      if (this.installmentNumber) {
        return `Installment #${this.installmentNumber}`;
      }
      return 'Installment Payment';
    }
    return 'Full Payment';
  }

  getCourseTitle(): string {
    return this.payment?.subscription?.course?.title || 'Course Subscription';
  }

  goBack(): void {
    this.router.navigate(['/my-subscriptions']);
  }
}
