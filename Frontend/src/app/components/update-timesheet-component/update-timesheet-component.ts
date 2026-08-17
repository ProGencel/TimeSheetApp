import {Component, DestroyRef, ElementRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {Project} from '../../models/project/Project';
import {ProjectService} from '../../services/project-service/project-service';
import {debounceTime, distinctUntilChanged, Subject, takeUntil} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';

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
  projects = signal<Project[]>([]);

  private timeSheetService = inject(TimeSheetService);
  private projectService = inject(ProjectService);
  private router = inject(Router);

  private id: number = 0;
  protected formGroup: FormGroup;

  private destroyRef = inject(DestroyRef);

  private searchSubject = new Subject<string>();

  @ViewChild('projectInput') projectInput!: ElementRef<HTMLInputElement>;

  constructor(private formBuilder: FormBuilder,private route: ActivatedRoute) {
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
        next: (response) => this.projects.set(response.content),
        error: (error) => console.log(error)
      });
    });

    this.searchSubject.next('');
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = Number(params['id']);
      this.timeSheetService.getTimeSheetById(this.id).subscribe({
        next: (response) => {
          this.formGroup.patchValue(response);
        },
        error: (error) => {
          console.log(error);
        }
      });
    });

  }

  onSearch(event: Event){
    const q = (event.target as HTMLInputElement).value;
    this.searchSubject.next(q);
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

  onSelectProject(project: Project): void {
    this.formGroup.patchValue({ projectId: project.id });
    this.projectInput.nativeElement.value = project.name;
  }
}
