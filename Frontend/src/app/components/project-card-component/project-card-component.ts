import {Component, EventEmitter, inject, Input, OnChanges, OnInit, Output, signal, SimpleChanges} from '@angular/core';
import {ProjectService} from '../../services/project-service/project-service';

@Component({
  selector: 'app-project-card-component',
  imports: [],
  templateUrl: './project-card-component.html',
  styleUrl: './project-card-component.css',
})
export class ProjectCardComponent implements OnChanges {
  @Input({ required: true }) projectId!: number;
  @Output() close = new EventEmitter<void>();

  protected name = signal<string>('');
  protected description = signal<string>('');
  protected username = signal<string>('');
  protected isFinished = signal<boolean>(false);
  protected isOwner = signal<boolean>(false);
  protected isChecked = signal<boolean>(false);

  private projectService = inject(ProjectService);

  ngOnChanges(): void {
    this.projectService.getProject(this.projectId).subscribe(project => {
      this.name.set(project.name);
      this.description.set(project.description);
      this.username.set(project.user.username);
      this.isFinished.set(project.finished);
      this.isOwnerFunc();
    });
  }

  onClose(): void
  {
    this.setFinished();
    this.close.emit();
  }

  isOwnerFunc(): void
  {
    this.projectService.isOwner(this.projectId).subscribe(result => {
      this.isOwner.set(result);
    });
  }

  setFinished(): void {
    if(this.isChecked())
    {
      this.projectService.setFinished(this.projectId).subscribe(result => {
        console.log(result);
      });
    }
  }

  onCheckbox(event: Event): void
  {
    const checked = (event.target as HTMLInputElement).checked;
    if (checked) {
      this.isChecked.set(true);
    } else {
      this.isChecked.set(false);
    }
  }

}
