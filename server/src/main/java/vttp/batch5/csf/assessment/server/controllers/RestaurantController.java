package vttp.batch5.csf.assessment.server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.util.Optional;

import vttp.batch5.csf.assessment.server.model.MenuItem;
import vttp.batch5.csf.assessment.server.model.OrderItem;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonArray;
import jakarta.json.JsonReader;

import java.io.StringReader;
import vttp.batch5.csf.assessment.server.services.RestaurantService;

@RestController
@RequestMapping("/api")
public class RestaurantController {


       @Value("${application.payment.url}")
       private String PAYMENT_URL;

       @Value("${application.payee.name}")
       private String PAYEE_NAME;

    @Autowired
    private RestaurantService restaurantService;

  @Autowired
  private RestTemplate restTemplate;

    // Retrieve all menu items
    @GetMapping("/menus")
    public ResponseEntity<?> getMenus() {
      System.out.println("fire fire fire");
        Optional<List<MenuItem>> menuItemsOptional = restaurantService.getMenu();
        if (menuItemsOptional.isPresent()) {
            return ResponseEntity.ok(menuItemsOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No menu items found.");
        }
    }

  // TODO: Task 4
  // Do not change the method's signature
    @PostMapping("/food_order")
    public ResponseEntity<String> postFoodOrder(@RequestBody String payload) {
        JsonReader jsonReader = Json.createReader(new StringReader(payload));
        JsonObject jsonPayload = jsonReader.readObject();
        String username = jsonPayload.getString("username");
        String password = jsonPayload.getString("password");
        List<OrderItem> orderItems = new ArrayList<>();
        JsonArray itemsArray = jsonPayload.getJsonArray("items");
        double paymentAmount = 0.0; // For testing, replace with actual amount
        for (JsonObject item : itemsArray.getValuesAs(JsonObject.class)) {
            String id = item.getString("id");
            double price = item.getJsonNumber("price").doubleValue();
            int quantity = item.getInt("quantity");
            paymentAmount += 1.0*price*quantity;
            OrderItem orderItem = new OrderItem(id, price, quantity);
            orderItems.add(orderItem);
        }
        // for (OrderItem item: orderItems) {System.out.println(item);}
        // System.out.println(paymentAmount);

        // inValidate request
        if (!restaurantService.validateUserCredentials(username, password)) {
            return ResponseEntity.status(401).body("{\"message\":\"invalid username and/or password\"}");
        }

        String orderId = generateOrderId();
        JsonObject paymentRequest = Json.createObjectBuilder()
                .add("order_id", orderId)
                .add("payer", username)
                .add("payee", PAYEE_NAME)
                .add("payment", paymentAmount)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");
        headers.set("X-Authenticate", username);
        HttpEntity<String> requestEntity = new HttpEntity<>(paymentRequest.toString(), headers);
        try {
            ResponseEntity<String> paymentResponse = restTemplate.exchange(PAYMENT_URL, HttpMethod.POST, requestEntity, String.class);
            JsonReader reader = Json.createReader(new StringReader(paymentResponse.getBody()));
            JsonObject jsonResponse = reader.readObject();
            String paymentId = jsonResponse.getString("payment_id");
            long timestamp = jsonResponse.getJsonNumber("timestamp").longValue();
            System.out.println("Payment Response: "+paymentResponse.getBody());
            System.out.println("Payment id: "+paymentId);
            System.out.println("Payment time: "+timestamp);
            try {
              restaurantService.updateDatabases(orderId,paymentId,username,paymentAmount,orderItems);
              JsonObject responseJson = Json.createObjectBuilder()
                      .add("orderId", orderId)
                      .add("paymentId", paymentId)
                      .add("total", paymentAmount)
                      .add("timeStamp", timestamp)
                      .build();
              return ResponseEntity.ok(responseJson.toString());
            } catch (Exception e) {
              return ResponseEntity.status(500).body(e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Payment failed: " + e.getMessage());
            return ResponseEntity.status(400).body(e.getMessage());
        }

    }

  //random stackoverflow pull LOL
    private String generateOrderId() {
        SecureRandom random = new SecureRandom();
        return random.ints(48, 122)
                     .filter(i -> (i <= 57) || (i >= 65 && i <= 90) || (i >= 97))
                     .limit(8)
                     .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                     .toString();
    }
}
