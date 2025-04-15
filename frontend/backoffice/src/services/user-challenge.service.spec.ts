import { TestBed } from '@angular/core/testing';

import { UserChallengeService } from './user-challenge.service';

describe('UserChallengeService', () => {
  let service: UserChallengeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserChallengeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
