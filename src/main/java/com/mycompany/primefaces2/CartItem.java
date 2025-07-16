/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import java.io.Serializable;
import java.math.BigDecimal;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer cartItemId;
    private Integer cartId;
    private String productType;
    private Integer productRefId;
    private String productName;
    private String productImagePath;
    private int quantity;
    private BigDecimal priceAtAddition;

    public CartItem() {
    }

    public CartItem(Integer cartId, String productType, Integer productRefId, String productName, String productImagePath, int quantity, BigDecimal priceAtAddition) {
        this.cartId = cartId;
        this.productType = productType;
        this.productRefId = productRefId;
        this.productName = productName;
        this.productImagePath = productImagePath;
        this.quantity = quantity;
        this.priceAtAddition = priceAtAddition;
    }

    public Integer getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(Integer cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getProductRefId() {
        return productRefId;
    }

    public void setProductRefId(Integer productRefId) {
        this.productRefId = productRefId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImagePath() {
        return productImagePath;
    }

    public void setProductImagePath(String productImagePath) {
        this.productImagePath = productImagePath;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtAddition() {
        return priceAtAddition;
    }

    public void setPriceAtAddition(BigDecimal priceAtAddition) {
        this.priceAtAddition = priceAtAddition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return quantity == cartItem.quantity &&
               productRefId.equals(cartItem.productRefId) &&
               productType.equals(cartItem.productType) &&
               cartId.equals(cartItem.cartId) &&
               priceAtAddition.equals(cartItem.priceAtAddition);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(cartId, productType, productRefId, quantity, priceAtAddition);
    }

    @Override
    public String toString() {
        return "CartItem{" +
               "cartItemId=" + cartItemId +
               ", cartId=" + cartId +
               ", productType='" + productType + '\'' +
               ", productRefId=" + productRefId +
               ", productName='" + productName + '\'' +
               ", quantity=" + quantity +
               ", priceAtAddition=" + priceAtAddition +
               '}';
    }
}

