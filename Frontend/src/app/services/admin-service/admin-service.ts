import {inject, Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {PageResponse} from '../../models/PageResponse';
import {TimeSheet} from '../../models/TimeSheet';
import {environment} from '../../../environments/environment.development';
import {HttpClient, HttpResponse} from '@angular/common/http';

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

  searchUsers(page: number, q: string) : Observable<PageResponse<TimeSheet>>{
    let url = `${environment.apiUrl}/admin/search_user?page=${page}`;
    if(q)
    {
      url+=`&q=${q}`;
    }

    return this.http.get<PageResponse<TimeSheet>>(url);
  }

  exportCsvUser(q: string | null): Observable<HttpResponse<Blob>> {
    let url = '';
    if(q)
    {
      url = `${environment.apiUrl}/admin/export?inputFormat=user&exportFormat=csv`;
    }
    else
    {
      url = `${environment.apiUrl}/admin/export?inputFormat=userq=${q}&exportFormat=csv`;
    }


    return this.http.get(url, { responseType: 'blob', observe: 'response'});
  }

  exportExcelUser(q: string | null): Observable<HttpResponse<Blob>> {
    let url = '';
    if(q)
    {
      url = `${environment.apiUrl}/admin/export?exportFormat=excel&inputFormat=user`;
    }
    else
    {
      url = `${environment.apiUrl}/admin/export?exportFormat=excel&q=${q}&inputFormat=user`;
    }

    return this.http.get(url, { responseType: 'blob', observe: 'response'});
  }
}
