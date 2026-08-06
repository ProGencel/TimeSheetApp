import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewTimesheetComponent } from './new-timesheet-component';

describe('NewTimesheetComponent', () => {
  let component: NewTimesheetComponent;
  let fixture: ComponentFixture<NewTimesheetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewTimesheetComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(NewTimesheetComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
