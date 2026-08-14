import {Component, EventEmitter, inject, Output, signal} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ProjectService} from '../../services/project-service/project-service';

@Component({
  selector: 'app-new-project-component',
  imports: [
    ReactiveFormsModule,
    FormsModule
  ],
  templateUrl: './new-project-component.html',
  styleUrl: './new-project-component.css',
})
export class NewProjectComponent {
  @Output() closeNew = new EventEmitter<void>();

  errorMessage = signal<string>('');

  formGroup: FormGroup;

  private projectService: ProjectService = inject(ProjectService);

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
    })

  }

  onSubmit() {
    this.projectService.saveProject(this.formGroup.value).subscribe({
      next: () => {
        this.onClose();
      },
      error: (error) => {
        this.errorMessage.set(error.error?.message || "Please try with different information");
        console.log(error);
      }
    });
  }

  onClose(): void
  {
    this.closeNew.emit();
  }

}
