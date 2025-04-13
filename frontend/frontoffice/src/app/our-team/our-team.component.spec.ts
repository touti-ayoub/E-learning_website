import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OurTeamComponent } from './our-team.component';

describe('OurTeamComponent', () => {
  let component: OurTeamComponent;
  let fixture: ComponentFixture<OurTeamComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OurTeamComponent]
    });
    fixture = TestBed.createComponent(OurTeamComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
