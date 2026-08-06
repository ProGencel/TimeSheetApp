import {Component, inject, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';

@Component({
  selector: 'app-new-timesheet-component',
  imports: [
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './new-timesheet-component.html',
  styleUrl: './new-timesheet-component.css',
})
export class NewTimesheetComponent {

  errorMessage = signal<string>('');

  formGroup: FormGroup;
  private timeSheetService = inject(TimeSheetService);
  private router = inject(Router);

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = this.formBuilder.group({
      date: ['', Validators.required],
      startTime: ['',Validators.required],
      endTime: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  onSubmit() {

    if(this.formGroup.valid) {
      this.timeSheetService.saveTimeSheet(this.formGroup.value).subscribe({
        next: (response) => {
          void this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.errorMessage.set(error.error?.message || "Please enter valid information");
          console.log(error);
        }
      })
    }
  }

}
