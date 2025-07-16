/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

/**
 *
 * @author michael
 */

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Named
@ApplicationScoped
public class OrderService implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer createOrder(Order order, List<OrderedItem> orderedItems) {
        Integer newOrderId = null;
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet rs = null;

        try {
            conn = mysqlConnector.getConnection();
            conn.setAutoCommit(false);

            String orderSql = "INSERT INTO orders ("
                            + "user_id, cart_id, order_date, status, total_amount, "
                            + "customer_email, customer_contact, first_name, last_name, "
                            + "shipping_address, shipping_city, shipping_postal_code, shipping_county_region"
                            + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);

            orderStmt.setObject(1, order.getUserId());
            orderStmt.setObject(2, order.getCartId());
            orderStmt.setTimestamp(3, Timestamp.valueOf(order.getOrderDate()));
            orderStmt.setString(4, order.getStatus());
            orderStmt.setBigDecimal(5, order.getTotalAmount());
            orderStmt.setString(6, order.getCustomerEmail());
            orderStmt.setString(7, order.getCustomerContact());
            orderStmt.setString(8, order.getFirstName());
            orderStmt.setString(9, order.getLastName());
            orderStmt.setString(10, order.getShippingAddress());
            orderStmt.setString(11, order.getShippingCity());
            orderStmt.setString(12, order.getShippingPostalCode());
            orderStmt.setString(13, order.getShippingCountyRegion());

            int affectedRows = orderStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating order failed, no rows affected.");
            }

            rs = orderStmt.getGeneratedKeys();
            if (rs.next()) {
                newOrderId = rs.getInt(1);
                order.setOrderId(newOrderId);
            } else {
                throw new SQLException("Creating order failed, no ID obtained.");
            }

            String itemSql = "INSERT INTO ordered_items ("
                           + "order_id, product_type, product_ref_id, product_name, "
                           + "product_image_path, quantity, price_at_time_of_addition"
                           + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);

            for (OrderedItem item : orderedItems) {
                itemStmt.setInt(1, newOrderId);
                itemStmt.setString(2, item.getProductType());
                itemStmt.setInt(3, item.getProductRefId());
                itemStmt.setString(4, item.getProductName());
                itemStmt.setString(5, item.getProductImagePath());
                itemStmt.setInt(6, item.getQuantity());
                itemStmt.setBigDecimal(7, item.getPriceAtTimeOfAddition());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            conn.commit();

        } catch (SQLException e) {
            System.err.println("Database error creating order: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Rollback failed: " + ex.getMessage());
                }
            }
            newOrderId = null;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); }
            try { if (orderStmt != null) orderStmt.close(); } catch (SQLException e) { System.err.println("Error closing Order Statement: " + e.getMessage()); }
            try { if (itemStmt != null) itemStmt.close(); } catch (SQLException e) { System.err.println("Error closing Item Statement: " + e.getMessage()); }
            try { if (conn != null) conn.close(); } catch (SQLException e) { System.err.println("Error closing Connection: " + e.getMessage()); }
        }
        return newOrderId;
    }
}




