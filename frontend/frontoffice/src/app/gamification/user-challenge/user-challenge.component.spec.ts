import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserChallengeComponent } from './user-challenge.component';

describe('UserChallengeComponent', () => {
  let component: UserChallengeComponent;
  let fixture: ComponentFixture<UserChallengeComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserChallengeComponent]
    });
    fixture = TestBed.createComponent(UserChallengeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
