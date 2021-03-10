import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment as env } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  private readonly apiUrl: string = env.apiUrl

  constructor(private readonly http: HttpClient) { }

  makeMove(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}board/move`, data);
  }

  startNew(data): Observable<any> {
    return this.http.post(`${this.apiUrl}board/new`, data);
  }

  getCurrent(): Observable<any> {
    return this.http.get(`${this.apiUrl}board/`);
  }

}
