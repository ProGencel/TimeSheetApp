import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {environment} from '../../../environments/environment.development';
import {TimeSheetSave} from '../../models/TimeSheetSave';
import {TimeSheet} from '../../models/TimeSheet';
import {TimeSheetResponse} from '../../models/TimeSheetResponse';
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
}
