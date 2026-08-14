import {Component, EventEmitter, inject, Output} from '@angular/core';
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
        console.log("Bla bla");
      }
    })
  }

  onClose(): void
  {
    this.closeNew.emit();
  }

}
