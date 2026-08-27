import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {TimeSheet} from '../../models/timesheet/TimeSheet';
import {environment} from '../../../environments/environment.development';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {ProjectDurationDto} from '../../models/project/ProjectDuration';
import {UserResponse} from '../../models/user/UserResponse';

@Injectable({
  providedIn: 'root',
})
export class AdminService {
  private http = inject(HttpClient);

  getTimeSheets(page: number): Observable<PageResponse<TimeSheet>> {
    return this.http.get<PageResponse<TimeSheet>>(environment.apiUrl+'/admin/list?page=' + page);
  }

  searchTimeSheetsByDate(page: number,date: string): Observable<PageResponse<TimeSheet>>
  {
    let url = `${environment.apiUrl}/admin/search_timesheets`;

    if(page)
    {
      url+=`?page=` + page;
      url += `&localDate=${date}`;
    }
    else
    {
      url += `?localDate=${date}`;
    }

    return this.http.get<PageResponse<TimeSheet>>(url);
  }

  searchUsers(page: number, q: string): Observable<PageResponse<UserResponse>> {
    return this.http.get<PageResponse<UserResponse>>(
      `${environment.apiUrl}/admin/search_user?page=${page}&q=${encodeURIComponent(q)}`
    );
  }

  getWeeklyHoursByUserId(userId: number): Observable<number> {
    return this.http.get<number>(`${environment.apiUrl}/admin/weekly-hours/${userId}`);
  }

  searchTimeSheetsByDateRange(
    page: number,
    startDate: string | null,
    endDate: string | null,
    username: string | null = null
  ): Observable<PageResponse<TimeSheet>> {
    let url = `${environment.apiUrl}/admin/search_timesheets?page=${page}`;

    if (username) {
      url += `&username=${encodeURIComponent(username)}`;
    }
    if (startDate) {
      url += `&startDate=${startDate}`;
    }
    if (endDate) {
      url += `&endDate=${endDate}`;
    }

    return this.http.get<PageResponse<TimeSheet>>(url);
  }

  exportCsvUser(q: string | null): Observable<any> {
    const query = q ? `&q=${encodeURIComponent(q)}` : '';
    return this.http.get(
      `${environment.apiUrl}/admin/export?exportFormat=csv&inputFormat=user${query}`,
      { observe: 'response', responseType: 'blob' as 'json' }
    );
  }

  exportExcelUser(q: string | null): Observable<any> {
    const query = q ? `&q=${encodeURIComponent(q)}` : '';
    return this.http.get(
      `${environment.apiUrl}/admin/export?exportFormat=excel&inputFormat=user${query}`,
      { observe: 'response', responseType: 'blob' as 'json' }
    );
  }

  // ---- EXPORT: TIMESHEET'LER (artık tarih aralığı ile) ----

  exportCsvTimesheet(startDate: string | null, endDate: string | null, q: string | null = null): Observable<any> {
    let url = `${environment.apiUrl}/admin/export?exportFormat=csv&inputFormat=timesheet`;
    if (q) url += `&q=${encodeURIComponent(q)}`;
    if (startDate) url += `&startDate=${startDate}`;
    if (endDate) url += `&endDate=${endDate}`;

    return this.http.get(url, { observe: 'response', responseType: 'blob' as 'json' });
  }

  exportExcelTimesheet(startDate: string | null, endDate: string | null, q: string | null = null): Observable<any> {
    let url = `${environment.apiUrl}/admin/export?exportFormat=excel&inputFormat=timesheet`;
    if (q) url += `&q=${encodeURIComponent(q)}`;
    if (startDate) url += `&startDate=${startDate}`;
    if (endDate) url += `&endDate=${endDate}`;

    return this.http.get(url, { observe: 'response', responseType: 'blob' as 'json' });
  }


  getTopThree(): Observable<ProjectDurationDto[]>
  {
    return this.http.get<ProjectDurationDto[]>(environment.apiUrl+'/admin/get_top_three');
  }
}
