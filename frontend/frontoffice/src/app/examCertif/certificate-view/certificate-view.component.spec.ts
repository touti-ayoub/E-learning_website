import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CertificateViewComponent } from './certificate-view.component';

describe('CertificateViewComponent', () => {
  let component: CertificateViewComponent;
  let fixture: ComponentFixture<CertificateViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CertificateViewComponent]
    });
    fixture = TestBed.createComponent(CertificateViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
