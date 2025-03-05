import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BadgeFormComponent } from './badge-form.component';

describe('BadgeFormComponent', () => {
  let component: BadgeFormComponent;
  let fixture: ComponentFixture<BadgeFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BadgeFormComponent]
    });
    fixture = TestBed.createComponent(BadgeFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
