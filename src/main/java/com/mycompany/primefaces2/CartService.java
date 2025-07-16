/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named
@ApplicationScoped
public class CartService implements Serializable {
    private static final long serialVersionUID = 1L;

    public Cart getOrCreateActiveCartForUser(Integer userId) {
        Cart cart = findActiveCartByUserId(userId);
        if (cart == null) {
            cart = createCart(userId, "ACTIVE");
        }
        return cart;
    }

    public Cart findActiveCartByUserId(Integer userId) {
        Cart cart = null;
        String sql = "SELECT cart_id, user_id, created_at, updated_at, status FROM carts WHERE user_id = ? AND status = 'ACTIVE'";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                cart = new Cart();
                cart.setCartId(rs.getInt("cart_id"));
                cart.setUserId(rs.getObject("user_id", Integer.class));
                cart.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cart.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                cart.setStatus(rs.getString("status"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return cart;
    }

    public Cart createCart(Integer userId, String status) {
        Cart newCart = new Cart();
        String sql = "INSERT INTO carts (user_id, created_at, updated_at, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setObject(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setString(4, status);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating cart failed, no rows affected.");
            }

            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                newCart.setCartId(rs.getInt(1));
                newCart.setUserId(userId);
                newCart.setCreatedAt(LocalDateTime.now());
                newCart.setUpdatedAt(LocalDateTime.now());
                newCart.setStatus(status);
            } else {
                throw new SQLException("Creating cart failed, no ID obtained.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return newCart;
    }

    public boolean updateCartStatus(Integer cartId, String newStatus) {
        if (cartId == null) return false;
        String sql = "UPDATE carts SET status = ?, updated_at = ? WHERE cart_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newStatus);
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, cartId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    public List<CartItem> getCartItems(Integer cartId) {
        List<CartItem> items = new ArrayList<>();
        if (cartId == null) return items;
        String sql = "SELECT cart_item_id, cart_id, product_type, product_ref_id, quantity, price_at_time_of_addition FROM cart_items WHERE cart_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            rs = stmt.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemId(rs.getInt("cart_item_id"));
                item.setCartId(rs.getInt("cart_id"));
                item.setProductType(rs.getString("product_type"));
                item.setProductRefId(rs.getInt("product_ref_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setPriceAtAddition(rs.getBigDecimal("price_at_time_of_addition"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return items;
    }

    public void addOrUpdateCartItem(Integer cartId, String productType, Integer productRefId, int quantity, double price) {
        if (cartId == null) {
            return;
        }

        String findSql = "SELECT cart_item_id, quantity FROM cart_items WHERE cart_id = ? AND product_type = ? AND product_ref_id = ?";
        String updateSql = "UPDATE cart_items SET quantity = quantity + ?, price_at_time_of_addition = ? WHERE cart_item_id = ?";
       
        String insertSql = "INSERT INTO cart_items (cart_id, product_type, product_ref_id, quantity, price_at_time_of_addition) VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement findStmt = null;
        PreparedStatement updateStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;

        try {
            conn = mysqlConnector.getConnection();
            conn.setAutoCommit(false);

            findStmt = conn.prepareStatement(findSql);
            findStmt.setInt(1, cartId);
            findStmt.setString(2, productType);
            findStmt.setInt(3, productRefId);
            rs = findStmt.executeQuery();

            if (rs.next()) {
                int existingCartItemId = rs.getInt("cart_item_id");
                updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setInt(1, quantity);
                updateStmt.setBigDecimal(2, BigDecimal.valueOf(price));
                updateStmt.setInt(3, existingCartItemId);
                updateStmt.executeUpdate();
            } else {
                insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setInt(1, cartId);
                insertStmt.setString(2, productType);
                insertStmt.setInt(3, productRefId);
                insertStmt.setInt(4, quantity);
                insertStmt.setBigDecimal(5, BigDecimal.valueOf(price));
                insertStmt.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (findStmt != null) findStmt.close(); } catch (SQLException e) {}
            try { if (updateStmt != null) updateStmt.close(); } catch (SQLException e) {}
            try { if (insertStmt != null) insertStmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    public void removeCartItem(Integer cartId, String productType, Integer productRefId) {
        if (cartId == null) return;
        String sql = "DELETE FROM cart_items WHERE cart_id = ? AND product_type = ? AND product_ref_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, cartId);
            stmt.setString(2, productType);
            stmt.setInt(3, productRefId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    public void updateCartItemQuantity(int cartItemId, int newQuantity) {
        String sql = "UPDATE cart_items SET quantity = ? WHERE cart_item_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, cartItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

    public void clearCart(Integer oldCartId) {
        if (oldCartId == null) {
            return;
        }

        Connection conn = null;
        PreparedStatement deleteItemsStmt = null;
        try {
            conn = mysqlConnector.getConnection();
            conn.setAutoCommit(false);

            String deleteItemsSql = "DELETE FROM cart_items WHERE cart_id = ?";
            deleteItemsStmt = conn.prepareStatement(deleteItemsSql);
            deleteItemsStmt.setInt(1, oldCartId);
            int deletedRows = deleteItemsStmt.executeUpdate();

            updateCartStatus(oldCartId, "ORDERED");

            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) {}
            }
        } finally {
            try { if (deleteItemsStmt != null) deleteItemsStmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
    }

   public String getProductName(String type, int productId) {
    String productName = "Unknown Product";
    String sql = "";
    String columnToRetrieve = ""; 

    if ("HARDWARE".equals(type)) {
        sql = "SELECT Name FROM Products WHERE product_id = ?";
        columnToRetrieve = "Name"; 
    } else if ("PACKAGE".equals(type)) {
        sql = "SELECT package_name FROM internet_packages WHERE package_id = ?";
        columnToRetrieve = "package_name"; 
    } else {
        return productName;
    }

    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try {
        conn = mysqlConnector.getConnection();
        stmt = conn.prepareStatement(sql);
        stmt.setInt(1, productId);
        rs = stmt.executeQuery();
        if (rs.next()) {
            productName = rs.getString(columnToRetrieve);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
        try { if (conn != null) conn.close(); } catch (SQLException e) {}
    }
    return productName;
}

    public String getProductImagePath(String type, int productId) {
        String imagePath = "resources/images/default.png";
        String sql = "";
        if ("HARDWARE".equals(type)) {
            sql = "SELECT image_path FROM products WHERE product_id = ?";
        } else if ("PACKAGE".equals(type)) {
            sql = "SELECT image_path FROM internet_packages WHERE package_id = ?";
        } else {
            return imagePath;
        }

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                imagePath = rs.getString("image_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return imagePath;
    }

    public int getProductStock(String type, int productId) {
        if (!"HARDWARE".equals(type)) {
            return 9999;
        }
        int stock = 0;
        String sql = "SELECT stock FROM products WHERE product_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = mysqlConnector.getConnection();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                stock = rs.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) {}
            try { if (stmt != null) stmt.close(); } catch (SQLException e) {}
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return stock;
    }
}

