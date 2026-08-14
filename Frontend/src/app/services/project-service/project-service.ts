import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProjectResponse} from '../../models/project/ProjectResponse';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment.development';
import {ProjectSave} from '../../models/project/ProjectSave';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private http = inject(HttpClient);

  getProject(id: number): Observable<ProjectResponse>
  {
    return this.http.get<ProjectResponse>(environment.apiUrl+'/project/get/'+id);
  }

  saveProject(projectSave: ProjectSave) : Observable<any>
  {
    return this.http.post<ProjectResponse>(environment.apiUrl+'/project/save', projectSave);
  }

}
