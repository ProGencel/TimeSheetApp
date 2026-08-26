import {Component, DestroyRef, ElementRef, EventEmitter, inject, Output, signal, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ProjectService} from '../../services/project-service/project-service';
import {debounceTime, distinctUntilChanged, Subject} from 'rxjs';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {UserResponse} from '../../models/user/UserResponse';
import {AdminService} from '../../services/admin-service/admin-service';

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
  users = signal<UserResponse[]>([]);
  selectedUsers = signal<UserResponse[]>([]);

  formGroup: FormGroup;

  private destroyRef = inject(DestroyRef);
  private projectService: ProjectService = inject(ProjectService);
  private adminService: AdminService = inject(AdminService);

  private searchSubject = new Subject<string>();

  @ViewChild('userInput') userInput!: ElementRef<HTMLInputElement>;

  constructor(private formBuilder: FormBuilder) {
    this.formGroup = this.formBuilder.group({
      name: ['', Validators.required],
      description: ['', Validators.required],
      member_ids: [[]],
    });

    this.searchSubject.pipe(
      debounceTime(100),
      distinctUntilChanged(),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(q => {
      this.adminService.searchUsers(0,q).subscribe({
        next: (response) => this.users.set(
          response.content.filter(user =>
            !this.selectedUsers().some(selected => selected.id === user.id))
        ),
        error: (error) => console.log(error)
      });
    });

    this.searchSubject.next('');
  }

  onSubmit() {
    if (this.formGroup.valid) {
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
  }

  onSearch(event: Event) {
    const q = (event.target as HTMLInputElement).value;
    this.searchSubject.next(q);
  }

  onSelectUser(user: UserResponse): void {
    this.selectedUsers.update(current => [...current, user]);
    this.formGroup.patchValue({
      member_ids: this.selectedUsers().map(u => u.id)
    });

    this.userInput.nativeElement.value = '';
    this.searchSubject.next('');
  }

  onRemoveUser(user: UserResponse): void {
    this.selectedUsers.update(current => current.filter(u => u.id !== user.id));
    this.formGroup.patchValue({
      member_ids: this.selectedUsers().map(u => u.id)
    });
  }

  onClose(): void {
    this.closeNew.emit();
  }
}
