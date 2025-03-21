import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from './environment/environment';

export interface MenuItem {
  id: string;
  name: string;
  description: string;
  price: number;
}

@Injectable({
  providedIn: 'root'
})
export class MenuService {
  private readonly backAddr =  environment.backAddr;
  private apiUrl = this.backAddr+'/api/menu';

  constructor(private http: HttpClient) {}

  getMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>(this.apiUrl, {
      headers: { 'Accept': 'application/json' }
    });
  }
}
