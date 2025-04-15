import { TestBed } from '@angular/core/testing';

import { PointServiceService } from './point-service.service';

describe('PointServiceService', () => {
  let service: PointServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PointServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
