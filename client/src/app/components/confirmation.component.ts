import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderService } from '../service/order.service';
import { Router } from '@angular/router';
@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrls: ['./confirmation.component.css']
})

export class ConfirmationComponent implements OnInit {
  orderId: string | null = null;
  paymentId: string | null = null;
  total: number | null = null;
  timestamp: Date = new Date();

  constructor(private route: ActivatedRoute,
              private orderService: OrderService,
             private router: Router) {}

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.orderId = params.get('orderId');
      this.paymentId = params.get('paymentId');
      this.total = +params.get('total')!;
    const timestampParam = params.get('timeStamp');
    if (timestampParam) {
      const timestampMs = +timestampParam;
      this.timestamp = new Date(timestampMs);
    } else {
      this.timestamp = new Date();
    }
    });
  }

  startOver(): void {
    this.orderService.clearOrderItems();
    this.router.navigate(['/']);
  }
}
