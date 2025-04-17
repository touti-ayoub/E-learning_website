import { Component, OnInit } from '@angular/core';
import { CouponService } from '../../../../service/mic2/coupon.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Coupon {
  id: number;
  code: string;
  discountPercentage: number;
  active: boolean;
  valid: boolean;
}

@Component({
  selector: 'app-coupon-list',
  templateUrl: './coupon-list.component.html',
  standalone: true,
  imports: [CommonModule, FormsModule],
  styleUrls: ['./coupon-list.component.scss']
})
export class CouponListComponent implements OnInit {
  // Data
  coupons: Coupon[] = [];
  filteredCoupons: Coupon[] = [];

  // Filters
  searchTerm: string = '';
  statusFilter: string = 'all';
  validityFilter: string = 'all';
  discountFilter: string = 'all';

  // Sorting
  sortColumn: string = '';
  sortDirection: string = 'asc';

  constructor(
    private couponService: CouponService,
    public router: Router,
  ) {}

  ngOnInit(): void {
    this.loadCoupons();
  }

  private loadCoupons() {
    this.couponService.getAllCoupons().subscribe(
      (data: Coupon[]) => {
        this.coupons = data;
        this.filteredCoupons = [...this.coupons];
        console.log('All coupons loaded:', data.length);
        console.log(data);
      },
      error => {
        console.error('Error loading coupons:', error);
      }
    );
  }

  applyFilters() {
    this.filteredCoupons = this.coupons.filter(coupon => {
      // Search by code
      const matchesSearch = this.searchTerm ?
        coupon.code.toLowerCase().includes(this.searchTerm.toLowerCase()) :
        true;

      // Filter by status
      const matchesStatus = this.statusFilter === 'all' ?
        true : (this.statusFilter === 'active' ? coupon.active : !coupon.active);

      // Filter by validity
      const matchesValidity = this.validityFilter === 'all' ?
        true : (this.validityFilter === 'valid' ? coupon.valid : !coupon.valid);

      // Filter by discount percentage
      let matchesDiscount = true;
      if (this.discountFilter === 'high') {
        matchesDiscount = coupon.discountPercentage >= 50;
      } else if (this.discountFilter === 'medium') {
        matchesDiscount = coupon.discountPercentage >= 20 && coupon.discountPercentage < 50;
      } else if (this.discountFilter === 'low') {
        matchesDiscount = coupon.discountPercentage < 20;
      }

      return matchesSearch && matchesStatus && matchesValidity && matchesDiscount;
    });

    // Apply current sort
    if (this.sortColumn) {
      this.applySorting();
    }
  }

  sort(column: string) {
    if (this.sortColumn === column) {
      // Toggle sort direction
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      // New column, default to ascending
      this.sortColumn = column;
      this.sortDirection = 'asc';
    }

    this.applySorting();
  }

  private applySorting() {
    this.filteredCoupons.sort((a: any, b: any) => {
      const valueA = a[this.sortColumn];
      const valueB = b[this.sortColumn];

      if (typeof valueA === 'string') {
        return this.sortDirection === 'asc'
          ? valueA.localeCompare(valueB)
          : valueB.localeCompare(valueA);
      } else {
        return this.sortDirection === 'asc'
          ? valueA - valueB
          : valueB - valueA;
      }
    });
  }

  clearSearch() {
    this.searchTerm = '';
    this.applyFilters();
  }

  resetFilters() {
    this.searchTerm = '';
    this.statusFilter = 'all';
    this.validityFilter = 'all';
    this.discountFilter = 'all';
    this.sortColumn = '';
    this.sortDirection = 'asc';
    this.filteredCoupons = [...this.coupons];
  }

  getDiscountClass(percentage: number): string {
    if (percentage >= 50) {
      return 'high-discount';
    } else if (percentage >= 25) {
      return 'medium-discount';
    } else {
      return 'low-discount';
    }
  }
}
