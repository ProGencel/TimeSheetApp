import {Component, inject, OnInit, signal} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-update-timesheet-component',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './update-timesheet-component.html',
  styleUrl: './update-timesheet-component.css',
})
export class UpdateTimesheetComponent implements OnInit {
  errorMessage = signal<string>('');

  formGroup: FormGroup;
  private timeSheetService = inject(TimeSheetService);
  private router = inject(Router);
  private id: number = 0;

  constructor(private formBuilder: FormBuilder,private route: ActivatedRoute) {
    this.formGroup = this.formBuilder.group({
      date: ['', Validators.required],
      startTime: ['',Validators.required],
      endTime: ['', Validators.required],
      description: ['', Validators.required],
      project: [''],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);this.timeSheetService.getTimeSheetById(this.id).subscribe({
        next: (response) => {
          this.formGroup.patchValue(response);
        },
        error: (error) => {
          console.log(error);
        }
      });
    });

  }

  onSubmit() {

    if(this.formGroup.valid) {
      this.timeSheetService.updateTimeSheet(this.formGroup.value,this.id).subscribe({
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
