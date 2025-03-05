import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BadgeListComponent } from './badge-list.component';

describe('BadgeListComponent', () => {
  let component: BadgeListComponent;
  let fixture: ComponentFixture<BadgeListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BadgeListComponent]
    });
    fixture = TestBed.createComponent(BadgeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
