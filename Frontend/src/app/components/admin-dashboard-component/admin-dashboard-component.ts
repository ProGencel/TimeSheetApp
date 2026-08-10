import {Component, computed, inject, OnInit, signal} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink} from '@angular/router';
import {AdminService} from '../../services/admin-service/admin-service';
import {TimeSheet} from '../../models/TimeSheet';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {NgClass} from '@angular/common';
import {UserResponse} from '../../models/UserResponse';

@Component({
  selector: 'app-admin-dashboard-component',
  imports: [
    FormsModule,
    NgClass
  ],
  templateUrl: './admin-dashboard-component.html',
  styleUrl: './admin-dashboard-component.css',
})
export class AdminDashboardComponent implements OnInit {
  private adminService = inject(AdminService);
  private date: string | null = null;
  private q: string | null = null;


  timesheets = signal<TimeSheet[]>([]);
  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  isLoading = signal<boolean>(false);
  users = signal<UserResponse[]>([]);
  viewMode = signal<'timesheet' | 'user'>('timesheet');


  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number)
  {
    let request$: Observable<PageResponse<TimeSheet>> | Observable<PageResponse<UserResponse>>;
    let mode: 'timesheet' | 'user';

    if(this.q)
    {
      request$ = this.adminService.searchUsers(page,this.q);
      mode = 'user';
    }
    else if(this.date == null)
    {
      request$ = this.adminService.getTimeSheets(page);
      mode = 'timesheet';
    }
    else
    {
      request$ = this.adminService.searchTimeSheetsByDate(page,this.date);
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
  }

  onSearchTimeSheets(date: string)
  {
    this.date = date;
    this.loadPage(0);
  }

  pageNumbers = computed(() => { // otomatik olarak bir signal değiştiğinde güncelleniyor
    const total = this.totalPages();
    const current = this.currentPage();
    const delta = 2; // mevcut sayfanın etrafında kaç numara gösterilecek
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
    this.date = null;
    this.q = null;
    this.loadPage(0);
  }

  onSearchUser(q: string)
  {
    this.q = q;
    this.loadPage(0);
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
        window.URL.revokeObjectURL(url);//İndirme işlemi için bellekte oluşan memory'i silip memory leak i engeller
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
        let fileName = 'users.xlsx'; // fallback

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

  onCsvTimeSheet() { /* yeni */ }
  onExcelTimeSheet() { /* yeni */ }
}
