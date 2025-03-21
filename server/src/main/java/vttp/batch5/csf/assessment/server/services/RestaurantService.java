package vttp.batch5.csf.assessment.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;

import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;
import vttp.batch5.csf.assessment.server.model.MenuItem;
import vttp.batch5.csf.assessment.server.model.Order;
import vttp.batch5.csf.assessment.server.model.OrderItem;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantService {

  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired RestaurantRepository restaurantRepository;

  // TODO: Task 2.2
  // You may change the method's signature
    public Optional<List<MenuItem>> getMenu() {
        List<MenuItem> menuItems = ordersRepository.getMenu();
        return Optional.ofNullable(menuItems);
    }

  // TODO: Task 4
    public boolean validateUserCredentials(String username, String password) {
        return restaurantRepository.validateUser(username, password);
    }

@Transactional
public void updateDatabases(String orderId, String paymentId, String username, double total, List<OrderItem> items) {
    try {
      System.out.println("addiing :"+orderId);
        restaurantRepository.insertOrder(orderId, paymentId, username, total);
        ordersRepository.insertOrder(orderId, paymentId, username, total, items);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


}
