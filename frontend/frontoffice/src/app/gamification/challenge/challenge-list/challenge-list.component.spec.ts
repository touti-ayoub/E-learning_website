import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeListComponent } from './challenge-list.component';

describe('ChallengeListComponent', () => {
  let component: ChallengeListComponent;
  let fixture: ComponentFixture<ChallengeListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ChallengeListComponent]
    });
    fixture = TestBed.createComponent(ChallengeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
