import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {TimeSheetService} from '../../services/timesheet-service/time-sheet-service';
import {TimeSheet} from '../../models/TimeSheet';
import {NgClass} from '@angular/common';
import {Router, RouterLink} from '@angular/router';
import {environment} from '../../../environments/environment.development';

@Component({
  selector: 'app-dashboard-component',
  imports: [
    NgClass,
    RouterLink
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

  pageNumbers = computed(() =>  {
    return Array.from({length: this.totalPages() },(_, i) => i);
  })

  ngOnInit() {
    this.loadPage(0);
  }

  loadPage(pageNumber: number): void{
    this.isLoading.set(true);

    this.timeSheetService.getTimeSheets(pageNumber).subscribe({
      next:(response) => {
        this.timesheets.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.isLoading.set(false);
      },
      error:(error) => {
        console.log(error);
        this.isLoading.set(false);
      }
    })
  }

  onUpdate(id: number): void {
    void this.router.navigate(['/timesheet/update',id]);
  }

}
