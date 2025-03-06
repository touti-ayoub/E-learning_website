import { TestBed } from '@angular/core/testing';

import { UserChallengeServiceService } from './user-challenge-service.service';

describe('UserChallengeServiceService', () => {
  let service: UserChallengeServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserChallengeServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
