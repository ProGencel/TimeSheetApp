import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ProjectResponse} from '../../models/project/ProjectResponse';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment.development';
import {ProjectSave} from '../../models/project/ProjectSave';
import { PageResponse } from "../../models/PageResponse";
import {Project} from '../../models/project/Project';

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

  isOwner(id: number): Observable<boolean>
  {
    return this.http.get<boolean>(environment.apiUrl+'/project/isOwner/'+id);
  }

  setFinished(id: number): Observable<any>
  {
    return this.http.put(environment.apiUrl+`/project/set_finished/${id}`, id);
  }

  searchProject(q: string, page: number): Observable<PageResponse<Project>>
  {
    return this.http.get<PageResponse<Project>>(environment.apiUrl+`/project/search?q=${q}&page=${page}`);
  }
}
