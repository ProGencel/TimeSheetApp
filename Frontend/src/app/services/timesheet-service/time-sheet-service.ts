import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {environment} from '../../../environments/environment.development';
import {TimeSheet} from '../../models/TimeSheet';

@Injectable({
  providedIn: 'root',
})
export class TimeSheetService {
  private http = inject(HttpClient);

  getTimeSheets(page: number): Observable<PageResponse<TimeSheet>> {
    return this.http.get<PageResponse<TimeSheet>>(environment.apiUrl+'/timesheet/list?page=' + page);
  }
}
