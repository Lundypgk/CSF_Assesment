package vttp.batch5.csf.assessment.server.repositories;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Collections;
import java.util.Optional;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public boolean validateUser(String username, String password) {
        String sql = "SELECT COUNT(*) FROM customers WHERE username = ? AND password = SHA2(?, 224)";
        try {
            Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username, password}, Integer.class);
            return count != null && count > 0;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    public void insertOrder(String orderId, String paymentId, String username, double total) {
        String sql = "INSERT INTO place_orders (order_id, payment_id, order_date, total, username) VALUES (?, ?, NOW(), ?, ?)";
        jdbcTemplate.update(sql, orderId, paymentId, total, username);
    }
}
