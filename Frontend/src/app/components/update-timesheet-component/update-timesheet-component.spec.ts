import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateTimesheetComponent } from './update-timesheet-component';

describe('UpdateTimesheetComponent', () => {
  let component: UpdateTimesheetComponent;
  let fixture: ComponentFixture<UpdateTimesheetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateTimesheetComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateTimesheetComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
