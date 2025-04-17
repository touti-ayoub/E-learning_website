import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { AuthService } from "../../../services/auth/auth.service";
import { SubscriptionService, SubCreatingRequest } from "../../../services/mic2/subscription.service";
import { CouponService } from "../../../services/mic2/coupon.service";
import { finalize } from 'rxjs/operators';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css']
})
export class SubscriptionComponent implements OnInit {
  courseId!: number;
  course: any;
  loading = false;
  error: string | null = null;
  paymentTypes = ['FULL', 'INSTALLMENTS'];
  selectedPaymentType = 'FULL';
  autoRenew = false;
  installments = 3; // Default value
  storedUsername: string | null = null;
  userId: number | null = null;

  // Coupon related properties
  couponCode: string = '';
  couponDiscount: number = 0;
  hasValidCoupon: boolean = false;
  validatingCoupon: boolean = false;
  couponMessage: string | null = null;
  couponError: boolean = false;
  discountedPrice: number = 0;

  courses = [
    { id: 1, title: 'Web Development', instructor: 'Moon', price: 300 },
    { id: 2, title: 'Angular+Spring', instructor: 'John Doe', price: 499 },
    { id: 3, title: 'ISTQB', instructor: 'Alice', price: 180 }
  ];
  currentDate = new Date();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private subscriptionService: SubscriptionService,
    private authService: AuthService,
    private couponService: CouponService
  ) {}

  ngOnInit() {
    this.courseId = Number(this.route.snapshot.paramMap.get('courseId'));
    this.course = this.courses.find(c => c.id === this.courseId);

    if (!this.course) {
      this.error = 'Course not found';
    }

    this.storedUsername = localStorage.getItem('username');
    if (!this.storedUsername) {
      this.error = 'You must be logged in to subscribe to a course';
      Swal.fire({
        title: 'Authentication Required',
        text: 'Please log in to continue with course subscription',
        icon: 'warning',
        confirmButtonText: 'Login'
      }).then(() => {
        this.router.navigate(['/login'], {
          queryParams: { returnUrl: this.router.url }
        });
      });
    } else {
      this.getUserId();
    }
  }

  private getUserId() {
    if (!this.storedUsername) return;

    this.loading = true;
    this.subscriptionService.getUserByUsername(this.storedUsername)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          this.userId = response.id;
          console.log(`User ID retrieved: ${this.userId}`);
        },
        error: (error) => {
          console.error('Error retrieving user:', error);
          this.error = 'Failed to retrieve user information';
        }
      });
  }

  /**
   * Validate coupon code with backend
   */
  validateCoupon(): void {
    if (!this.couponCode || this.validatingCoupon || this.hasValidCoupon) {
      return;
    }

    // Basic validation
    if (this.couponCode.trim().length < 3) {
      this.couponError = true;
      this.couponMessage = 'Please enter a valid coupon code';
      return;
    }

    this.validatingCoupon = true;
    this.couponMessage = null;
    this.couponError = false;

    this.couponService.validateCoupon(this.couponCode, this.courseId)
      .pipe(finalize(() => this.validatingCoupon = false))
      .subscribe({
        next: (response) => {
          console.log('Coupon validation response:', response);
          if (response.valid) {
            // Store discount information
            this.hasValidCoupon = true;
            this.couponDiscount = response.discountPercentage;
            this.discountedPrice = response.discountedPrice;
            this.couponMessage = `Coupon applied! You saved $${this.getDiscountAmount().toFixed(2)}`;
            this.couponError = false;

            // Store coupon in localStorage
            localStorage.setItem(`coupon_${this.courseId}`, this.couponCode);
            localStorage.setItem(`coupon_discount_${this.courseId}`, String(this.couponDiscount));
            localStorage.setItem(`coupon_price_${this.courseId}`, String(this.discountedPrice));

            // Show success message to user
            Swal.fire({
              title: 'Coupon Applied!',
              text: `You got a ${this.couponDiscount}% discount`,
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
          } else {
            this.resetCoupon();
            this.couponMessage = response.message || 'Invalid coupon code';
            this.couponError = true;
          }
        },
        error: (error) => {
          console.error('Error validating coupon:', error);
          this.resetCoupon();
          this.couponMessage = error.error?.message || 'Failed to validate coupon';
          this.couponError = true;
        }
      });
  }

  /**
   * Reset coupon state
   */
  resetCoupon(): void {
    this.hasValidCoupon = false;
    this.couponDiscount = 0;
    this.discountedPrice = 0;
    this.couponMessage = null;
    this.couponError = false;
    this.couponCode = '';

    // Clear stored coupon
    localStorage.removeItem(`coupon_${this.courseId}`);
    localStorage.removeItem(`coupon_discount_${this.courseId}`);
    localStorage.removeItem(`coupon_price_${this.courseId}`);
  }

  /**
   * Calculate the discounted price based on coupon
   */
  getDiscountedPrice(): number {
    if (!this.hasValidCoupon || !this.course) {
      return this.course?.price || 0;
    }
    
    if (this.discountedPrice > 0) {
      return this.discountedPrice;
    }
    
    // Calculate on the fly if we don't have a pre-calculated value
    const discount = (this.course.price * this.couponDiscount) / 100;
    return this.course.price - discount;
  }

  /**
   * Get the discount amount
   */
  getDiscountAmount(): number {
    if (!this.hasValidCoupon || !this.course) {
      return 0;
    }
    return this.course.price - this.getDiscountedPrice();
  }

  /**
   * Get the final price (original or discounted)
   */
  getFinalPrice(): number {
    return this.hasValidCoupon ? this.getDiscountedPrice() : (this.course?.price || 0);
  }

  /**
   * Calculate monthly payment including any coupon discount
   */
  getMonthlyPayment(): number {
    if (this.selectedPaymentType !== 'INSTALLMENTS' || !this.installments || this.installments <= 0) {
      return 0;
    }
    return this.getFinalPrice() / this.installments;
  }

  /**
   * Calculate monthly installment amount for a specific number of months
   */
  getMonthlyInstallmentAmount(months: number): number {
    if (!this.course) return 0;
    return this.getFinalPrice() / months;
  }

  proceedToPayment() {
    if (!this.course) {
      this.error = 'Course not found';
      return;
    }

    if (!this.userId) {
      this.error = 'User information not found';
      return;
    }

    this.loading = true;
    this.error = null;

    // Log coupon details for debugging
    console.log('Proceeding to payment with coupon details:', {
      hasValidCoupon: this.hasValidCoupon,
      couponCode: this.couponCode,
      couponDiscount: this.couponDiscount,
      originalPrice: this.course.price,
      discountedPrice: this.getDiscountedPrice()
    });

    const request: SubCreatingRequest = {
      userId: this.userId,
      courseId: this.courseId,
      paymentType: this.selectedPaymentType,
      autoRenew: this.autoRenew,
      installments: this.selectedPaymentType === 'INSTALLMENTS' ? this.installments : undefined,
      couponCode: this.hasValidCoupon ? this.couponCode : undefined
    };

    this.subscriptionService.createSubscription(request)
      .pipe(finalize(() => this.loading = false))
      .subscribe({
        next: (response) => {
          console.log('Subscription created:', response);
          this.navigateToPayment(response.id);
        },
        error: (error) => {
          console.error('Error creating subscription:', error);
          this.error = error.message || 'Failed to create subscription';
          Swal.fire({
            title: 'Already Subscribed',
            text: 'You already have an active subscription for this course.',
            icon: 'info',
            confirmButtonText: 'View My Subscriptions',
            showCancelButton: true,
            cancelButtonText: 'Stay Here'
          }).then((result) => {
            if (result.isConfirmed) {
              this.router.navigate(['/my-subscriptions']);
            }
          });
        }
      });
  }

  /**
 * Navigate to payment page with discount information
 */
navigateToPayment(subscriptionId: number) {
  if (!this.course) {
    this.error = 'Course not found';
    return;
  }

  // Calculate the exact prices with correct precision
  const originalPrice = this.course.price;
  let discountedPrice = originalPrice;
  let discountPercentage = 0;
  
  // If coupon is validated, calculate the correct discounted price
  if (this.hasValidCoupon && this.couponDiscount > 0) {
    discountPercentage = this.couponDiscount;
    const discountAmount = (originalPrice * discountPercentage) / 100;
    discountedPrice = originalPrice - discountAmount;
    
    // Round to 2 decimal places for consistency
    discountedPrice = Math.round((discountedPrice + Number.EPSILON) * 100) / 100;
    
    console.log('Coupon discount calculation:', {
      originalPrice,
      discountPercentage,
      discountAmount,
      discountedPrice
    });
  }
  
  // Log what we're sending to the payment page
  console.log('Navigating to payment with:', {
    subscriptionId,
    originalPrice,
    discountedPrice,
    discountPercentage,
    hasDiscount: this.hasValidCoupon,
    couponCode: this.couponCode
  });
  
  // Include all discount information in query params
  this.router.navigate(['/payment'], {
    queryParams: {
      subscriptionId: subscriptionId,
      amount: discountedPrice,  // The final price after discount
      originalAmount: this.hasValidCoupon ? originalPrice : undefined,
      discountPercentage: this.hasValidCoupon ? discountPercentage : undefined,
      hasDiscount: this.hasValidCoupon,
      paymentType: this.selectedPaymentType,
      installments: this.selectedPaymentType === 'INSTALLMENTS' ? this.installments : undefined,
      couponCode: this.hasValidCoupon ? this.couponCode : undefined
    }
  });
}

  goo() {
    this.router.navigate(['/courses'])
  }
}
