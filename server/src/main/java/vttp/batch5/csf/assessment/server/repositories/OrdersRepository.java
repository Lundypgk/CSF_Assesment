package vttp.batch5.csf.assessment.server.repositories;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.core.query.Query;

import vttp.batch5.csf.assessment.server.model.MenuItem;
import vttp.batch5.csf.assessment.server.model.Order;
import vttp.batch5.csf.assessment.server.model.OrderItem;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Repository
public class OrdersRepository {


    @Autowired
    private MongoTemplate mongoTemplate;

  // TODO: Task 2.2
  // You may change the method's signature
  // Write the native MongoDB query in the comment below
  // Native MongoDB query: db.menus.find()
  public List<MenuItem> getMenu() {
        Query query = new Query();
        return mongoTemplate.find(query, MenuItem.class);
  }

  // TODO: Task 4
  // Write the native MongoDB query for your access methods in the comment below
  //  Native MongoDB query here
  //  // db.orders.insertOne({
  //     order_id: "exampleOrderId",
  //     payment_id: "examplePaymentId",
  //     username: "exampleUsername",
  //     total: exampleTotal,
  //     items: [
  //         { id: "item1", price: 10.0, quantity: 2 },
  //         { id: "item2", price: 5.0, quantity: 4 }
  //     ]
  // })
  // Method to insert the order into the orders collection
  public void insertOrder(String orderId, String paymentId, String username, double total, List<OrderItem> items) {
    System.out.println("adding to mongo");
      Order order = new Order(orderId, paymentId, username, total, items);
      mongoTemplate.insert(order, "orders"); // Inserting into the `orders` collection
  }
  
}
