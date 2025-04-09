import { Component, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PaymentService } from '../../../services/mic2/payment.service';
import { StripeService } from '../../../services/mic2/stripe.service';
import { finalize, catchError, switchMap } from 'rxjs/operators';
import { Subscription, from, of } from 'rxjs';
import Swal from 'sweetalert2';
import { Stripe, StripeCardElement } from '@stripe/stripe-js';

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
export class PaymentComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('stripeCardElement') stripeCardElement!: ElementRef;
  
  // Stripe related properties
  stripe: Stripe | null = null;
  cardElement: StripeCardElement | null = null;
  clientSecret: string | null = null;
  stripeError: string | null = null;
  
  // System info
  currentUser = 'iitsMahdii';
  currentDate = new Date('2025-04-08 23:35:08');
  
  // Component state
  loading = false;
  processing = false;
  loadingPaymentIntent = false;
  error: string | null = null;
  success = false;
  stripeLoaded = false;
  stripeInitialized = false;
  cardElementMounted = false;
  retryCount = 0;
  
  // Payment data
  paymentId?: number;
  payment?: PaymentResponse;
  subscriptionId?: number;
  scheduleId?: number;
  installmentNumber?: number;
  amount?: number;
  paymentType = 'FULL';  // Default to FULL payment
  
  // Installment options
  numberOfInstallments: number = 3; // Default number of installments
  installmentOptions: number[] = [1, 2, 3, 6, 12]; // Available options
  
  // Calculated values
  installmentAmount: number = 0;
  
  // Form data & validation
  cardDetails = {
    holder: '',
  };
  
  cardHolderInvalid = false;
  cardHolderTouched = false;
  cardComplete = false;
  
  // Subscriptions for cleanup
  private subscriptions = new Subscription();
  
  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private paymentService: PaymentService,
    private stripeService: StripeService
  ) {}
  
  ngOnInit(): void {
    // Get current user
    this.currentUser = localStorage.getItem('username') || 'iitsMahdii';
    
    // Get query parameters first
    this.getQueryParams();
    
    // Initialize Stripe by getting the singleton instance
    this.initializeStripe();
    
    // Calculate installment amount if the payment type is INSTALLMENTS
    if (this.amount && this.paymentType === 'INSTALLMENTS') {
      this.calculateInstallmentAmount();
    }
    
    // Load payment data
    if (this.subscriptionId) {
      this.loadPaymentDetails();
    } else if (this.paymentId) {
      this.loadExistingPayment();
    } else if (this.scheduleId) {
      this.loadSchedulePayment();
    } else {
      this.error = 'Missing required payment information';
    }
  }
  
  ngAfterViewInit(): void {
    // Setup card element after view is ready
    setTimeout(() => this.setupStripeCardElement(), 300);
  }
  
  ngOnDestroy(): void {
    // Clean up all subscriptions to prevent memory leaks
    this.subscriptions.unsubscribe();
    
    // Unmount Stripe card element if it exists
    if (this.cardElement) {
      this.cardElement.unmount();
    }
  }
  
  initializeStripe(): void {
    // Get the single Stripe instance from the service
    this.subscriptions.add(
      this.stripeService.getStripe().subscribe(
        stripe => {
          console.log('Got Stripe instance from service');
          this.stripe = stripe;
          this.stripeLoaded = true;
          this.stripeInitialized = true;
          
          // If the view is initialized, set up card element
          if (this.stripeCardElement && !this.cardElementMounted) {
            this.setupStripeCardElement();
          }
        },
        error => {
          console.error('Failed to get Stripe instance:', error);
          this.error = 'Payment system could not be initialized. Please refresh and try again.';
        }
      )
    );
  }
  
  setupStripeCardElement(): void {
    // Only proceed if we have the Stripe instance and DOM element
    if (!this.stripe || !this.stripeCardElement || !this.stripeCardElement.nativeElement) {
      if (this.retryCount < 3) {
        this.retryCount++;
        console.log(`Retry ${this.retryCount}/3: Waiting for Stripe and DOM...`);
        setTimeout(() => this.setupStripeCardElement(), 500);
      } else {
        console.error('Failed to set up card element after retries');
      }
      return;
    }
    
    try {
      // Create card element using the singleton Stripe instance
      console.log('Creating card element...');
      const elements = this.stripe.elements();
      
      this.cardElement = elements.create('card', {
        style: {
          base: {
            color: '#32325d',
            fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
            fontSmoothing: 'antialiased',
            fontSize: '16px',
            '::placeholder': {
              color: '#aab7c4'
            }
          },
          invalid: {
            color: '#fa755a',
            iconColor: '#fa755a'
          }
        },
        hidePostalCode: true
      });
      
      // Mount card element to DOM
      console.log('Mounting card element...');
      this.cardElement.mount(this.stripeCardElement.nativeElement);
      this.cardElementMounted = true;
      
      // Listen for changes
      this.cardElement.on('change', (event) => {
        console.log('Card element change:', event.complete ? 'complete' : 'incomplete', event);
        this.stripeError = event.error ? event.error.message : null;
        this.cardComplete = event.complete;
      });
      
      console.log('Card element mounted successfully');
    } catch (error) {
      console.error('Error setting up card element:', error);
      this.error = 'Could not initialize payment form. Please refresh and try again.';
    }
  }
  
  private getQueryParams(): void {
    this.subscriptions.add(
      this.route.queryParams.subscribe(params => {
        this.paymentId = params['paymentId'] ? +params['paymentId'] : undefined;
        this.subscriptionId = params['subscriptionId'] ? +params['subscriptionId'] : undefined;
        this.scheduleId = params['scheduleId'] ? +params['scheduleId'] : undefined;
        this.installmentNumber = params['installmentNumber'] ? +params['installmentNumber'] : undefined;
        this.amount = params['amount'] ? +params['amount'] : undefined;
        this.paymentType = params['paymentType'] || 'FULL';

        console.log('Query params loaded:', {
          paymentId: this.paymentId,
          subscriptionId: this.subscriptionId,
          scheduleId: this.scheduleId,
          installmentNumber: this.installmentNumber,
          amount: this.amount,
          paymentType: this.paymentType
        });
      })
    );
  }

  calculateInstallmentAmount(): void {
    if (!this.amount) return;
    
    // Simple calculation: total amount divided by number of installments
    this.installmentAmount = this.amount / this.numberOfInstallments;
    console.log(`Amount: ${this.amount} divided into ${this.numberOfInstallments} installments = ${this.installmentAmount} per installment`);
  }
  
  // When user changes the number of installments
  onInstallmentChange(event: any): void {
    this.numberOfInstallments = parseInt(event.target.value, 10);
    this.calculateInstallmentAmount();
  }

  private loadPaymentDetails(): void {
    if (!this.subscriptionId) {
      this.error = 'Subscription ID is required';
      return;
    }

    this.loading = true;
    this.error = null;
    
    console.log(`Creating payment with type ${this.paymentType}, installments: ${this.numberOfInstallments}`);
    
    this.subscriptions.add(
      this.paymentService.initializePayment(this.subscriptionId, this.paymentType, this.numberOfInstallments)
        .pipe(
          finalize(() => {
            this.loading = false;
          }),
          catchError(error => {
            this.error = `Failed to load payment details: ${error.message || 'Unknown error'}`;
            console.error('Error creating payment:', error);
            return of(null);
          })
        )
        .subscribe({
          next: (response) => {
            if (!response) return;
            
            this.payment = response;
            this.paymentId = response.id;
            console.log('Payment created:', this.payment);
            
            // Process payment schedules to find the first PENDING one
            if (this.payment.paymentSchedules && this.payment.paymentSchedules.length > 0) {
              this.processPaymentSchedules();
            }
            
            // Create payment intent now that we have the payment ID
            this.createPaymentIntent();
          }
        })
    );
  }

  private loadExistingPayment(): void {
    if (!this.paymentId) {
      this.error = 'Payment ID is required';
      return;
    }

    this.loading = true;
    this.error = null;
    
    this.subscriptions.add(
      this.paymentService.getPaymentById(this.paymentId)
        .pipe(
          finalize(() => {
            this.loading = false;
          }),
          catchError(error => {
            this.error = `Failed to load payment: ${error.message || 'Unknown error'}`;
            console.error('Error loading payment:', error);
            return of(null);
          })
        )
        .subscribe({
          next: (response) => {
            if (!response) return;
            
            this.payment = response;
            console.log('Existing payment loaded:', this.payment);
            
            // If there are payment schedules, set up installment information
            if (this.payment.paymentSchedules && this.payment.paymentSchedules.length > 0) {
              this.paymentType = 'INSTALLMENTS';
              this.numberOfInstallments = this.payment.paymentSchedules.length;
              this.processPaymentSchedules();
            }
            
            // Create payment intent after loading payment
            this.createPaymentIntent();
          }
        })
    );
  }
  
  // Process payment schedules to find the first pending one
  private processPaymentSchedules(): void {
    console.log('Processing payment schedules:', this.payment?.paymentSchedules);
    
    if (!this.payment?.paymentSchedules || this.payment.paymentSchedules.length === 0) {
      console.log('No payment schedules found');
      return;
    }
    
    // Sort schedules by installment number
    const schedules = [...this.payment.paymentSchedules].sort((a, b) => 
      a.installmentNumber - b.installmentNumber);
    
    console.log('Sorted schedules:', schedules);
    
    // Find first pending schedule
    const firstPendingSchedule = schedules.find(s => s.status === 'PENDING');
    
    if (firstPendingSchedule) {
      console.log('Found first pending schedule:', firstPendingSchedule);
      this.scheduleId = firstPendingSchedule.id;
      this.installmentAmount = firstPendingSchedule.amount;
      this.installmentNumber = firstPendingSchedule.installmentNumber;
    } else {
      console.log('No pending schedules found');
      // Still calculate installment amount even if no pending schedules
      if (this.payment.amount && this.payment.paymentSchedules.length > 0) {
        this.installmentAmount = this.payment.amount / this.payment.paymentSchedules.length;
      }
    }
  }
  
  // Method to handle loading payment details for a schedule
  private loadSchedulePayment(): void {
    if (!this.scheduleId) {
      this.error = 'Schedule ID is required';
      return;
    }

    this.loading = true;
    this.error = null;
    
    // This is an installment payment
    this.paymentType = 'INSTALLMENTS';
    
    // Load the specific schedule details
    this.subscriptions.add(
      this.paymentService.getScheduleById(this.scheduleId)
        .pipe(
          finalize(() => {
            this.loading = false;
          }),
          catchError(error => {
            this.error = `Failed to load schedule: ${error.message || 'Unknown error'}`;
            console.error('Error loading schedule:', error);
            return of(null);
          })
        )
        .subscribe({
          next: (schedule) => {
            if (!schedule) return;
            
            // Set schedule information
            this.installmentAmount = schedule.amount;
            this.installmentNumber = schedule.installmentNumber;
            this.paymentId = schedule.payment?.id;
            
            console.log('Schedule loaded:', schedule);
            
            // Create payment intent for this schedule
            this.createPaymentIntent();
          }
        })
    );
  }
  
  createPaymentIntent(): void {
    console.log('Creating payment intent with:', {
      paymentId: this.paymentId,
      scheduleId: this.scheduleId,
      paymentType: this.paymentType
    });
    
    if (this.paymentType === 'INSTALLMENTS') {
      // For a new installment payment being created
      if (!this.scheduleId && this.paymentId) {
        // We need to fetch the schedules for this payment
        this.loadingPaymentIntent = true;
        this.subscriptions.add(
          this.paymentService.getPaymentSchedules(this.paymentId)
            .pipe(
              finalize(() => this.loadingPaymentIntent = false),
              catchError(error => {
                this.error = `Failed to load payment schedules: ${error.message || 'Unknown error'}`;
                console.error('Error loading schedules:', error);
                return of([]);
              })
            )
            .subscribe({
              next: (schedules) => {
                if (!schedules || schedules.length === 0) {
                  this.error = 'No payment schedules found for this payment';
                  return;
                }
                
                // Sort and find first pending
                const sortedSchedules = [...schedules].sort((a, b) => 
                  a.installmentNumber - b.installmentNumber);
                
                const firstPending = sortedSchedules.find(s => s.status === 'PENDING');
                
                if (firstPending) {
                  this.scheduleId = firstPending.id;
                  this.installmentAmount = firstPending.amount;
                  this.installmentNumber = firstPending.installmentNumber;
                  
                  // Now create the payment intent with the schedule ID
                  this.createStripePaymentIntent();
                } else {
                  this.error = 'No pending installments found for this payment';
                }
              }
            })
        );
        return;
      }
      
      if (!this.scheduleId) {
        // This is a completely new installment payment - it's OK, we just need to create the full payment first
        console.log('Creating a new installment payment');
        this.createFullPaymentIntent();
        return;
      }
      
      // We have a specific scheduleId - create installment payment intent
      this.createInstallmentPaymentIntent();
    } else {
      // Regular full payment
      if (!this.paymentId) {
        this.error = 'Payment ID is required';
        return;
      }
      
      this.createFullPaymentIntent();
    }
  }
  
  // Helper methods to simplify payment intent creation
  private createFullPaymentIntent(): void {
    if (!this.paymentId) {
      this.error = 'Payment ID is required';
      return;
    }
    
    this.loadingPaymentIntent = true;
    this.error = null;
    
    this.subscriptions.add(
      this.paymentService.createFullStripePayment(this.paymentId)
        .pipe(
          finalize(() => this.loadingPaymentIntent = false),
          catchError(error => {
            this.error = `Failed to set up payment: ${error.message || 'Unknown error'}`;
            console.error('Error creating payment intent:', error);
            return of(null);
          })
        )
        .subscribe({
          next: (response) => {
            if (!response) return;
            
            this.clientSecret = response.clientSecret;
            console.log('Full payment intent created:', response);
          }
        })
    );
  }
  
  private createInstallmentPaymentIntent(): void {
    if (!this.scheduleId) {
      this.error = 'Schedule ID is required for installment payment';
      return;
    }
    
    this.loadingPaymentIntent = true;
    this.error = null;
    
    this.subscriptions.add(
      this.paymentService.createInstallmentStripePayment(this.scheduleId)
        .pipe(
          finalize(() => this.loadingPaymentIntent = false),
          catchError(error => {
            this.error = `Failed to set up installment payment: ${error.message || 'Unknown error'}`;
            console.error('Error creating installment payment intent:', error);
            return of(null);
          })
        )
        .subscribe({
          next: (response) => {
            if (!response) return;
            
            this.clientSecret = response.clientSecret;
            console.log('Installment payment intent created:', response);
          }
        })
    );
  }
  
  // Generic method to create appropriate payment intent based on current state
  private createStripePaymentIntent(): void {
    if (this.scheduleId) {
      this.createInstallmentPaymentIntent();
    } else if (this.paymentId) {
      this.createFullPaymentIntent();
    } else {
      this.error = 'Missing payment or installment information';
    }
  }
  
  onSubmit(): void {
    // Reset errors
    this.error = null;
    this.stripeError = null;
    
    // Mark fields as touched for validation
    this.markAllFieldsAsTouched();

    // Log validation state
    console.log('Submit validation:', {
      cardComplete: this.cardComplete,
      cardHolderValid: !this.cardHolderInvalid,
      stripeInitialized: this.stripeInitialized,
      clientSecret: this.clientSecret ? 'Available' : 'Missing',
      paymentId: this.paymentId,
      scheduleId: this.scheduleId,
      paymentType: this.paymentType
    });

    // Validate the form
    if (!this.validatePaymentForm()) {
      return;
    }
    
    // System requirements check
    if (!this.stripe || !this.cardElement || !this.clientSecret) {
      this.error = 'Payment system is not fully initialized. Please refresh and try again.';
      return;
    }

    this.processing = true;
    
    const billingDetails = {
      name: this.cardDetails.holder
    };
    
    console.log('Processing payment with Stripe...', {
      paymentType: this.paymentType,
      paymentId: this.paymentId,
      scheduleId: this.scheduleId
    });
    
    // Process the payment with Stripe
    this.subscriptions.add(
      this.paymentService.processStripePayment(
        this.clientSecret,
        this.cardElement,
        this.paymentId,
        this.scheduleId,
        this.subscriptionId,
        billingDetails
      ).pipe(
        finalize(() => {
          this.processing = false;
        }),
        catchError(error => {
          this.error = `Payment processing failed: ${error.message || 'Unknown error'}`;
          this.showErrorAlert();
          console.error('Payment error:', error);
          return of({ success: false, error: error.message });
        })
      ).subscribe({
        next: (result) => {
          console.log('Payment result:', result);
          
          if (result.success) {
            this.success = true;
            this.showSuccessAlert('Payment successful!');
            
            // Redirect to success page after a delay
            setTimeout(() => {
              this.router.navigate(['/payment-success'], {
                queryParams: {
                  paymentId: this.paymentId,
                  scheduleId: this.scheduleId,
                  type: this.paymentType === 'INSTALLMENTS' ? 'INSTALLMENT' : 'FULL'
                }
              });
            }, 1500);
          } else {
            this.error = result.error || 'Payment failed. Please try again.';
            this.showErrorAlert();
          }
        }
      })
    );
  }

  // Rest of your component methods...

  markAllFieldsAsTouched(): void {
    this.cardHolderTouched = true;
    this.validateCardHolder();
  }

  validateCardHolder(): void {
    this.cardHolderInvalid = !this.cardDetails.holder || this.cardDetails.holder.trim().length < 3;
  }

  validatePaymentForm(): boolean {
    this.validateCardHolder();
    
    // Check if the card is complete according to Stripe
    if (!this.cardComplete) {
      this.stripeError = 'Please complete the credit card information';
      return false;
    }
    
    if (this.cardHolderInvalid) {
      return false;
    }
    
    return true;
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

  retryPayment(): void {
    // Reset error states
    this.error = null;
    this.stripeError = null;
    
    // Recreate payment intent
    this.createPaymentIntent();
    
    // If the card element has issues, try to remount it
    if (!this.cardElementMounted) {
      setTimeout(() => this.setupStripeCardElement(), 500);
    }
  }

  getDisplayAmount(): string {
    const currencySymbol = this.payment?.currency === 'EUR' ? '€' : '$';

    // For installment payment, show the installment amount
    if (this.paymentType === 'INSTALLMENTS') {
      if (this.installmentAmount) {
        return `${currencySymbol}${this.installmentAmount.toFixed(2)}`;
      }
      
      // If we have a payment with schedules, use the first schedule's amount
      if (this.payment?.paymentSchedules && this.payment.paymentSchedules.length > 0) {
        const firstSchedule = this.payment.paymentSchedules[0];
        return `${currencySymbol}${firstSchedule.amount.toFixed(2)}`;
      }
      
      // If we have an amount but no installment amount calculated yet
      if (this.amount && this.numberOfInstallments) {
        return `${currencySymbol}${(this.amount / this.numberOfInstallments).toFixed(2)}`;
      }
    }
    
    // For regular payment or specific schedule payment
    if (this.scheduleId && this.amount) {
      return `${currencySymbol}${this.amount.toFixed(2)}`;
    } else if (this.payment?.amount) {
      return `${currencySymbol}${this.payment.amount.toFixed(2)}`;
    } else if (this.amount) {
      return `${currencySymbol}${this.amount.toFixed(2)}`;
    }

    return `${currencySymbol}0.00`;
  }
  
  getTotalAmount(): string {
    const currencySymbol = this.payment?.currency === 'EUR' ? '€' : '$';
    
    if (this.payment?.amount) {
      return `${currencySymbol}${this.payment.amount.toFixed(2)}`;
    } else if (this.amount) {
      return `${currencySymbol}${this.amount.toFixed(2)}`;
    }
    
    return `${currencySymbol}0.00`;
  }

  getPaymentDescription(): string {
    if (this.paymentType === 'INSTALLMENTS') {
      if (this.installmentNumber) {
        return `Installment #${this.installmentNumber}`;
      }
      return `${this.numberOfInstallments} Installments`;
    }
    return 'Full Payment';
  }

  getCourseTitle(): string {
    return this.payment?.subscription?.course?.title || 'Course Subscription';
  }

  goBack(): void {
    this.router.navigate(['/my-subscriptions']);
  }
  
  // Helper method to check if payment form can be displayed
  canShowPaymentForm(): boolean {
    return !this.loading && 
           !this.loadingPaymentIntent && 
           !this.success;
  }
  
  // Helper method to determine if submit button should be disabled
  isSubmitDisabled(): boolean {
    return this.processing || 
           !this.clientSecret || 
           !this.cardComplete || 
           this.cardHolderInvalid;
  }
}