import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { OrderService } from '../service/order.service';
import { MenuItem } from '../service/menu.service';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrls: ['./place-order.component.css']
})
export class PlaceOrderComponent implements OnInit {
  orderItems: MenuItem[] = [];
  totalOrderPrice: number = 0;
  orderForm: FormGroup;

  constructor(
    private orderService: OrderService,
    private router: Router,
    private fb: FormBuilder
  ) {
    this.orderForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.orderItems = this.orderService.getOrderItems();
    this.calculateTotalOrderPrice();
  }

  calculateTotalOrderPrice(): void {
    this.totalOrderPrice = this.orderItems.reduce((sum, item) => {
      return sum + (item.price * (item.quantity || 0));
    }, 0);
  }

  startOver(): void {
    this.orderService.clearOrderItems();
    this.router.navigate(['/']);
  }

  confirmOrder(): void {
    if (this.orderForm.valid) {
      const username = this.orderForm.get('username')?.value;
      const password = this.orderForm.get('password')?.value;

      const items = this.orderItems.map(item => ({
        id: item.id,
        price: item.price,
        quantity: item.quantity,
      })).filter(item => item.quantity > 0); // include only items that have been ordered

      this.orderService.confirmOrder(username, password, items).subscribe(
        response => {
          console.log('Order confirmed!', response);
          const orderId = response.orderId;
          const paymentId = response.paymentId;
          const total = response.total;
          const timestamp =  response.timestamp;
          this.router.navigate(['/confirmation', { orderId, paymentId, total,timestamp }]);
        },
        error => {
          const errorMessage = error.error?.message || 'An error occurred while confirming your order.';
          alert(errorMessage);
        }
      );
    }
  }
}
