import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {AdminService} from '../../services/admin-service/admin-service';
import {TimeSheet} from '../../models/timesheet/TimeSheet';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {NgClass} from '@angular/common';
import {UserResponse} from '../../models/user/UserResponse';
import {AuthService} from '../../services/auth-service/auth-service';
import {ProjectCardComponent} from '../project-card-component/project-card-component';
import {ProjectDurationDto} from '../../models/project/ProjectDuration';
import {NewProjectComponent} from '../new-project-component/new-project-component';

@Component({
  selector: 'app-admin-dashboard-component',
  imports: [
    FormsModule,
    NgClass,
    ProjectCardComponent,
    NewProjectComponent
  ],
  templateUrl: './admin-dashboard-component.html',
  styleUrl: './admin-dashboard-component.css',
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private startDate: string | null = null;
  private endDate: string | null = null;
  private username: string | null = null;
  private q: string | null = null;
  protected userSearchActive = false;

  protected isModalOpen = false;
  protected selectedProjectId: number | null = null;
  protected isWeeklyHoursModalOpen = signal(false);
  protected selectedUsername: string | null = null;
  protected weeklyMinutes = signal<number | null>(null);
  protected isWeeklyHoursLoading = signal(false);


  timesheets = signal<TimeSheet[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);
  users = signal<UserResponse[]>([]);
  viewMode = signal<'timesheet' | 'user'>('timesheet');
  topThreeProjects = signal<ProjectDurationDto[]>([]);
  isNewProjectModalOpen = signal(false);


  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number)
  {
    let request$: Observable<PageResponse<TimeSheet | UserResponse>>;
    let mode: 'timesheet' | 'user';

    if (this.userSearchActive) {
      request$ = this.adminService.searchUsers(page, this.q ?? '');
      mode = 'user';
    } else {
      request$ = this.adminService.searchTimeSheetsByDateRange(page, this.startDate, this.endDate, this.username);
      mode = 'timesheet';
    }

    this.isLoading.set(true);
    this.currentPage.set(page);
    request$.subscribe({
      next: (response: any) => {
        this.isLoading.set(false);
        this.currentPage.set(page);
        this.totalPages.set(response.totalPages);
        this.viewMode.set(mode);

        if (mode === 'user') {
          this.users.set(response.content);
        } else {
          this.timesheets.set(response.content);
        }
      }
    });
    this.adminService.getTopThree().subscribe({
      next: (response: any) => {
        this.topThreeProjects.set(response);
      }
    })
  }

  weeklyHoursFormatted = computed(() => {
    const minutes = this.weeklyMinutes();
    if (minutes === null) {
      return null;
    }
    return {
      hours: Math.floor(minutes / 60),
      remainingMinutes: minutes % 60,
    };
  });

  topThreeProjectsFormatted = computed(() =>
    this.topThreeProjects().map(project => ({
      ...project,
      hours: Math.floor(project.minutes / 60),
      remainingMinutes: project.minutes % 60,
    }))
  );

  onSearchTimeSheets(username: string, startDate: string, endDate: string)
  {
    this.username = username ? username : null;
    this.startDate = startDate ? startDate : null;
    this.endDate = endDate ? endDate : null;
    this.userSearchActive = false;
    this.loadPage(0);
  }

  closeWeeklyHoursModal(): void
  {
    this.isWeeklyHoursModalOpen.set(false);
    this.selectedUsername = null;
    this.weeklyMinutes.set(null);
  }

  onClickUser(userId: number, username: string): void
  {
    this.selectedUsername = username;
    this.isWeeklyHoursModalOpen.set(true);
    this.isWeeklyHoursLoading.set(true);
    this.weeklyMinutes.set(null);

    this.adminService.getWeeklyHoursByUserId(userId).subscribe({
      next: (minutes) => {
        this.weeklyMinutes.set(minutes);
        this.isWeeklyHoursLoading.set(false);
      },
      error: (error) => {
        console.log(error);
        this.isWeeklyHoursLoading.set(false);
      }
    });
  }

  pageNumbers = computed(() => {
    const total = this.totalPages();
    const current = this.currentPage();
    const delta = 2;
    const range: (number | '...')[] = [];

    const start = Math.max(0, current - delta);
    const end = Math.min(total - 1, current + delta);

    if (start > 0) {
      range.push(0);
      if (start > 1) range.push('...');
    }

    for (let i = start; i <= end; i++) {
      range.push(i);
    }

    if (end < total - 1) {
      if (end < total - 2) range.push('...');
      range.push(total - 1);
    }

    return range;
  });

  onClean()
  {
    this.startDate = null;
    this.endDate = null;
    this.username = null;
    this.q = null;
    this.userSearchActive = false;
    this.loadPage(0);
  }

  onSearchUser(q: string)
  {
    this.q = q;
    this.userSearchActive = true;
    this.loadPage(0);
  }

  logout()
  {
    this.authService.logout()
    void this.router.navigate(['/login']);
  }

  onCsvTimeSheet() {
    this.adminService.exportCsvTimesheet(this.startDate, this.endDate, this.username).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'timesheets.csv';

        if (contentDisposition) {
          const match = contentDisposition.match(/filename=(.+)/);
          if (match && match[1]) {
            fileName = match[1].trim();
          }
        }

        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();

        window.URL.revokeObjectURL(url); // Memory leak'i engellemek için bellekte oluşan URL'yi sil
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onExcelTimeSheet() {
    this.adminService.exportExcelTimesheet(this.startDate, this.endDate, this.username).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'timesheets.xlsx';

        if (contentDisposition) {
          const match = contentDisposition.match(/filename=(.+)/);
          if (match && match[1]) {
            fileName = match[1].trim();
          }
        }

        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();

        window.URL.revokeObjectURL(url); // Memory leak'i engellemek için bellekte oluşan URL'yi sil
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onCsvUser()
  {
    this.adminService.exportCsvUser(this.q).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'users.csv'; // fallback

        if (contentDisposition) {
          const match = contentDisposition.match(/filename=(.+)/);
          if (match && match[1]) {
            fileName = match[1].trim();
          }
        }

        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url); // İndirme işlemi için bellekte oluşan memory'i silip memory leak i engeller
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onExcelUser()
  {
    this.adminService.exportExcelUser(this.q).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'users.xlsx';

        if (contentDisposition) {
          const match = contentDisposition.match(/filename=(.+)/);
          if (match && match[1]) {
            fileName = match[1].trim();
          }
        }

        const blob = response.body as Blob;
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = fileName;
        a.click();
        window.URL.revokeObjectURL(url); // İndirme işlemi için bellekte oluşan memory'i silip memory leak'i engeller
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onClickProject(id: number): void
  {
    this.selectedProjectId = id;
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.selectedProjectId = null;
  }

  onCloseNewModal(): void {
    this.isNewProjectModalOpen.set(false);
  }

  onClickNewProject(): void
  {
    this.isNewProjectModalOpen.set(true);
  }

}
