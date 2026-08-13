import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {environment} from '../../../environments/environment.development';
import {TimeSheetSave} from '../../models/timesheet/TimeSheetSave';
import {TimeSheet} from '../../models/timesheet/TimeSheet';
import {TimeSheetResponse} from '../../models/timesheet/TimeSheetResponse';
@Injectable({
  providedIn: 'root',
})
export class TimeSheetService {
  private http = inject(HttpClient);

  getTimeSheets(page: number): Observable<PageResponse<TimeSheet>> {
    return this.http.get<PageResponse<TimeSheet>>(environment.apiUrl+'/timesheet/list?page=' + page);
  }

  saveTimeSheet(timesheet: TimeSheetSave): Observable<TimeSheetSave> {
    return this.http.post<TimeSheetSave>(environment.apiUrl+'/timesheet/save', timesheet);
  }

  updateTimeSheet(timesheet: TimeSheetSave,id: number): Observable<TimeSheetResponse> {
    return this.http.put<TimeSheetResponse>(`${environment.apiUrl}/timesheet/update/${id}`, timesheet);
  }

  getTimeSheetById(id: number): Observable<TimeSheetResponse> {
    return this.http.get<TimeSheetResponse>(`${environment.apiUrl}/timesheet/get/${id}`);
  }

  searchTimeSheets(page: number,startDate: string, endDate: string): Observable<PageResponse<TimeSheet>>
  {
    return this.http.get<PageResponse<TimeSheet>>(environment.apiUrl+`/timesheet/search?page=` + page + `&startDate=` + startDate + '&endDate=' + endDate);
  }

  exportCsv(startDate?: string | null, endDate?: string | null): Observable<HttpResponse<Blob>> {
    let url = `${environment.apiUrl}/timesheet/export?format=csv`;

    if (startDate) {
      url += `&startDate=${startDate}`;
    }
    if (endDate) {
      url += `&endDate=${endDate}`;
    }

    return this.http.get(url, { responseType: 'blob', observe: 'response'});
  }

  exportExcel(startDate?: string | null, endDate?: string | null): Observable<HttpResponse<Blob>> {
    let url = `${environment.apiUrl}/timesheet/export?format=excel`;

    if (startDate) {
      url += `&startDate=${startDate}`;
    }
    if (endDate) {
      url += `&endDate=${endDate}`;
    }

    return this.http.get(url, { responseType: 'blob', observe: 'response'});
  }

  getMinutes(): Observable<number>
  {
    return this.http.get<number>(`${environment.apiUrl}/timesheet/get_duration`);
  }

}
