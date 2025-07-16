/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import java.io.Serializable;
import java.util.Objects;


public class ShoppingCart implements Serializable {

    private static final long serialVersionUID = 1L; 
    private String type;
    private int id;
    private String name;
    private double price;
    private int quantity;
    private String imagePath;
    private int cartItemId;

    public ShoppingCart() {
    }

    public ShoppingCart(String type, int id, String name, double price, int quantity, String imagePath) {
        this.type = type;
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }

    public ShoppingCart(CartItem cartItem, String productName, String productImagePath) {
        this.cartItemId = cartItem.getCartItemId();
        this.type = cartItem.getProductType();
        this.id = cartItem.getProductRefId();
        this.name = productName;
        this.price = cartItem. getPriceAtAddition().doubleValue();
        this.quantity = cartItem.getQuantity();
        this.imagePath = productImagePath;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(int cartItemId) {
        this.cartItemId = cartItemId;
    }

    public double getTotalItemPrice() {
        return this.price * this.quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShoppingCart shoppingCart = (ShoppingCart) o; 
        return id == shoppingCart.id && type.equals(shoppingCart.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, id);
    }
}
