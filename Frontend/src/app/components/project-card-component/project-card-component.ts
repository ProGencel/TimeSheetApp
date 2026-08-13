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

  private projectService = inject(ProjectService);

  ngOnChanges(): void {
    this.projectService.getProject(this.projectId).subscribe(project => {
      this.name.set(project.name);
      this.description.set(project.description);
      this.username.set(project.user.username);
      this.isFinished.set(project.isFinished);
    });
  }

  onClose(): void
  {
    this.close.emit();
  }

}
