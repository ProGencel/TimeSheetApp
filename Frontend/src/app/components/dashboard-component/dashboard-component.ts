import {Component, computed, inject, OnInit, signal} from '@angular/core';
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
  isSearch = signal<boolean>(false);
  searchStartDate: string | null = null;
  searchEndDate: string | null = null;

  pageNumbers = computed(() =>  {
    return Array.from({length: this.totalPages() },(_, i) => i);
  })

  ngOnInit() {
    this.loadPage(0);
  }

  loadPage(page: number) {
    this.currentPage.set(page);
    this.isLoading.set(true);

    const request$ = this.searchStartDate && this.searchEndDate
      ? this.timeSheetService.searchTimeSheets(page, this.searchStartDate, this.searchEndDate)
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
    this.searchStartDate = startDate;
    this.searchEndDate = endDate;
    this.loadPage(0);
  }
}
