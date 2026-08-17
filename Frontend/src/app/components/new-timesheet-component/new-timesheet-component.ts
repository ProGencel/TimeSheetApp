import {Component, DestroyRef, ElementRef, inject, signal, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Router, RouterLink} from '@angular/router';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {debounceTime, distinctUntilChanged, Subject} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {ProjectService} from '../../services/project-service/project-service';
import {Project} from '../../models/project/Project';

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
  projects = signal<Project[]>([]);

  formGroup: FormGroup;

  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private timeSheetService = inject(TimeSheetService);
  private projectService = inject(ProjectService);

  private searchSubject = new Subject<string>();

  @ViewChild('projectInput') projectInput!: ElementRef<HTMLInputElement>;

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = this.formBuilder.group({
      date: ['', Validators.required],
      startTime: ['',Validators.required],
      endTime: ['', Validators.required],
      description: ['', Validators.required],
      projectId: [''],
    });

    this.searchSubject.pipe(
      debounceTime(100),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(q => {
      this.projectService.searchProject(q, 0).subscribe({
        next: (response) => this.projects.set(
          response.content.filter(project => !project.finished)),
        error: (error) => console.log(error)
      });
    });

    this.searchSubject.next('');
  }

  onSubmit() {

    if(this.formGroup.valid) {
      this.timeSheetService.saveTimeSheet(this.formGroup.value).subscribe({
        next: () => {
          void this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.errorMessage.set(error.error?.message || "Please enter valid information");
          console.log(error);
        }
      })
    }
  }

  onSearch(event: Event) {
    const q = (event.target as HTMLInputElement).value;
    this.searchSubject.next(q);
  }

  onSelectedProject(project: Project): void {
    this.formGroup.patchValue({ projectId: project.id });
    this.projectInput.nativeElement.value = project.name;
  }

}
