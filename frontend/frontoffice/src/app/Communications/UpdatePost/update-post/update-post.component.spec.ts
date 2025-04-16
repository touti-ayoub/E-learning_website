import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePostComponent } from './update-post.component';

describe('UpdatePostComponent', () => {
  let component: UpdatePostComponent;
  let fixture: ComponentFixture<UpdatePostComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UpdatePostComponent]
    });
    fixture = TestBed.createComponent(UpdatePostComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
