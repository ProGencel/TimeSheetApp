import {Component, computed, ElementRef, inject, OnInit, signal, ViewChild} from '@angular/core';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {TimeSheet} from '../../models/TimeSheet';
import {NgClass} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {environment} from '../../../environments/environment.development';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-dashboard-component',
  imports: [
    NgClass,
    RouterLink,
    FormsModule
  ],
  templateUrl: './dashboard-component.html',
  styleUrl: './dashboard-component.css',
})
export class DashboardComponent implements OnInit {
  private timeSheetService = inject(TimeSheetService);
  private router = inject(Router);

  timesheets = signal<TimeSheet[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);
  startDate: string | null = null;
  endDate: string | null = null;

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
      this.isLoading.set(false);
    });
  }

  onUpdate(id: number): void {
    void this.router.navigate(['/timesheet/update',id]);
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
}
