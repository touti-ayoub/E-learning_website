import { TestBed } from '@angular/core/testing';

<<<<<<<< HEAD:frontend/backoffice/src/services/badge.service.spec.ts
import { BadgeService } from './badge.service';

describe('BadgeService', () => {
  let service: BadgeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BadgeService);
========
import { StripeService } from './stripe.service';

describe('StripeService', () => {
  let service: StripeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StripeService);
>>>>>>>> 317f252d30a826dcb506fde504be389d34e9b868:frontend/frontoffice/src/services/mic2/stripe.service.spec.ts
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
