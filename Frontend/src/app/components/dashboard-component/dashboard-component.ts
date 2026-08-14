import {Component, computed, ElementRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {TimeSheet} from '../../models/timesheet/TimeSheet';
import {NgClass} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {FormsModule} from '@angular/forms';
import {AuthService} from '../../services/auth-service/auth-service';
import {ProjectCardComponent} from '../project-card-component/project-card-component';
import {NewProjectComponent} from '../new-project-component/new-project-component';

@Component({
  selector: 'app-dashboard-component',
  imports: [
    NgClass,
    RouterLink,
    FormsModule,
    ProjectCardComponent,
    NewProjectComponent
  ],
  templateUrl: './dashboard-component.html',
  styleUrl: './dashboard-component.css',
})
export class DashboardComponent implements OnInit {
  private router = inject(Router);
  private timeSheetService = inject(TimeSheetService);
  private authService = inject(AuthService);

  timesheets = signal<TimeSheet[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);

  startDate: string | null = null;
  endDate: string | null = null;
  totalWorkedMinutes: number = 0;
  workedHours: number = 0;
  workedMinutes: number = 0;
  isModalOpen = false;
  selectedProjectId: number | null = null;
  isNewProjectModalOpen: boolean = false;

  @ViewChild('startDateInput') startDateInput!: ElementRef<HTMLInputElement>;
  @ViewChild('endDateInput') endDateInput!: ElementRef<HTMLInputElement>;

  pageNumbers = computed(() =>  {
    return Array.from({length: this.totalPages() },(_, i) => i);
  })

  ngOnInit() {
    this.loadPage(0);
  }

  loadPage(page: number) {
    this.currentPage.set(page);
    this.isLoading.set(true);

    const request$ = this.startDate && this.endDate
      ? this.timeSheetService.searchTimeSheets(page, this.startDate, this.endDate)
      : this.timeSheetService.getTimeSheets(page);

    request$.subscribe(response => {
      this.timesheets.set(response.content);
      this.totalPages.set(response.totalPages);
      this.updateWorkedDuration();
    });
  }

  onUpdate(id: number): void {
    void this.router.navigate(['/timesheet/update',id]);
  }

  logout()
  {
    this.authService.logout()
    void this.router.navigate(['/login']);
  }

  onSearch(startDate: string, endDate: string) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.loadPage(0);
  }

  onClean() {
    this.startDateInput.nativeElement.value = '';
    this.endDateInput.nativeElement.value = '';
    this.startDate = null;
    this.endDate = null;
    this.loadPage(0);
  }

  onCsv() {
    this.timeSheetService.exportCsv(this.startDate, this.endDate).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'timesheets.csv'; // fallback

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
        window.URL.revokeObjectURL(url);//İndirme işlemi için bellekte oluşan memory'i silip memory leak i engeller
      },
      error: (error) => {
        console.log(error);
      }
    });
  }

  onExcel() {
    this.timeSheetService.exportExcel(this.startDate, this.endDate).subscribe({
      next: (response) => {
        const contentDisposition = response.headers.get('Content-Disposition');
        let fileName = 'timesheets.xlsx'; // fallback

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

  updateWorkedDuration()
  {
    this.timeSheetService.getMinutes().subscribe({
      next: (response) => {
        this.totalWorkedMinutes = response.valueOf();
        this.workedMinutes = this.totalWorkedMinutes % 60;
        this.workedHours = Math.floor(this.totalWorkedMinutes / 60);
        console.log(this.totalWorkedMinutes + this.workedHours + this.workedMinutes);

        this.isLoading.set(false);
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
    this.isNewProjectModalOpen = false;
    this.selectedProjectId = null;
  }

  onClickNewProject(): void
  {
    this.isNewProjectModalOpen = true;
  }
}
