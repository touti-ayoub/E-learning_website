import { TestBed } from '@angular/core/testing';

import { ChallengeServiceService } from './challenge-service.service';

describe('ChallengeServiceService', () => {
  let service: ChallengeServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChallengeServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
