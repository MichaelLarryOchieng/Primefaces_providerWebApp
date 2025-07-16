/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

/**
 *
 * @author michael
 */
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderedItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderItemId;
    private Integer orderId;
    private String productType;
    private Integer productRefId;
    private String productName;
    private String productImagePath;
    private Integer quantity;
    private BigDecimal priceAtTimeOfAddition;

    public OrderedItem() {
    }

    public Integer getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Integer orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtTimeOfAddition() {
        return priceAtTimeOfAddition;
    }

    public void setPriceAtTimeOfAddition(BigDecimal priceAtTimeOfAddition) {
        this.priceAtTimeOfAddition = priceAtTimeOfAddition;
    }
}

