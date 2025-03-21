import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MenuItem } from './menu.service';
import { Observable } from 'rxjs';
import {environment} from '../environment/environment';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private backAddr = environment.backAddr;
  private apiUrl = this.backAddr+'/api/food_order';
  private selectedItems: MenuItem[] = [];

  constructor(private http: HttpClient) {}

  confirmOrder(username: string, password: string, items: Array<{ id: string, price: number, quantity: number }>): Observable<any> {
    const body = {
      username: username,
      password: password,
      items: items
    };

    return this.http.post<any>(this.apiUrl, body, {
      headers: { 'Content-Type': 'application/json', 'Accept': 'application/json' }
    });
  }

  setOrderItems(items: MenuItem[]): void {
    this.selectedItems = items;
  }

  getOrderItems(): MenuItem[] {
    return this.selectedItems;
  }

  clearOrderItems(): void {
    this.selectedItems = [];
  }


}
