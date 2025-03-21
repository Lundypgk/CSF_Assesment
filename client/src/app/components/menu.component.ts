import { Component, OnInit } from '@angular/core';
import { MenuService, MenuItem} from '../service/menu.service';
import { OrderService } from '../service/order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent {
  // TODO: Task 2
  menuItems: MenuItem[] = [];
  totalQuantity: number = 0;
  totalPrice: number = 0;

  constructor(private menuService: MenuService,
             private router: Router,
             private orderService: OrderService) {}

  ngOnInit(): void {
    this.loadMenuItems();
  }

  loadMenuItems(): void {
    this.menuService.getMenuItems().subscribe(
      (data: MenuItem[]) => {
        this.menuItems = data.map(item => ({ ...item, quantity: 0 }));
        this.calculateTotalPrice();
      },
      (error) => {
        console.error('Error fetching menu items', error);
      }
    );
  }

  calculateTotalPrice(): void {
      this.totalPrice = this.menuItems.reduce((sum, item) => {
          return sum + (item.quantity ? item.price * item.quantity : 0);
      }, 0);
  }

  addItem(itemId: string): void {
    const item = this.menuItems.find(i => i.id === itemId);
    if (item) {
      item.quantity = (item.quantity || 0) + 1;
      this.updateTotalQuantity();
        this.calculateTotalPrice();
    }
  }

  removeItem(itemId: string): void {
    const item = this.menuItems.find(i => i.id === itemId);
    if (item && (item.quantity || 0) > 0) {
      item.quantity = (item.quantity || 0) - 1;
      this.updateTotalQuantity();
        this.calculateTotalPrice();

    }
  }

  updateTotalQuantity(): void {
    this.totalQuantity = this.menuItems.reduce((sum, item) => sum + (item.quantity || 0), 0);
  }

  placeOrder(): void {
    const selectedMenuItems = this.menuItems.filter(item => item.quantity > 0);
    this.orderService.setOrderItems(selectedMenuItems);
    this.router.navigate(['/place-order']);
  }
}
