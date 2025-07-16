/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

/**
 *
 * @author michael
 */
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.annotation.PostConstruct; // Add this import

@Named("receiptBean")
@SessionScoped
public class ReceiptBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer orderId;
    private String customerEmail;
    private String firstName;
    private String lastName;
    private String shippingAddress;
    private String shippingCity;
    private String shippingPostalCode;
    private String shippingCountyRegion;
    private BigDecimal totalAmount;
    private List<OrderedItem> orderedItems;
    private LocalDateTime orderDate;

    @PostConstruct 
    public void init() {
        System.out.println("DEBUG: ReceiptBean @PostConstruct called. Initial orderId: " + orderId);
       
        if (orderedItems == null) {
            orderedItems = new java.util.ArrayList<>();
        }
    }

    public ReceiptBean() {
        System.out.println("DEBUG: ReceiptBean constructor called.");
    }

    public void setConfirmationDetails(
            Integer orderId,
            String customerEmail,
            String firstName,
            String lastName,
            String shippingAddress,
            String shippingCity,
            String shippingPostalCode,
            String shippingCountyRegion,
            BigDecimal totalAmount,
            List<OrderedItem> orderedItems) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
        this.firstName = firstName;
        this.lastName = lastName;
        this.shippingAddress = shippingAddress;
        this.shippingCity = shippingCity;
        this.shippingPostalCode = shippingPostalCode;
        this.shippingCountyRegion = shippingCountyRegion;
        this.totalAmount = totalAmount;
        this.orderedItems = orderedItems;
        this.orderDate = LocalDateTime.now();

        System.out.println("DEBUG: ReceiptBean.setConfirmationDetails called. Order ID set to: " + this.orderId);
        System.out.println("DEBUG: Number of ordered items set: " + (this.orderedItems != null ? this.orderedItems.size() : 0));
    }

    public Integer getOrderId() {
        System.out.println("DEBUG: ReceiptBean.getOrderId() called. Current Order ID: " + orderId);
        return orderId;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public String getShippingCountyRegion() {
        return shippingCountyRegion;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public List<OrderedItem> getOrderedItems() {
        System.out.println("DEBUG: ReceiptBean.getOrderedItems() called. Returning " + (orderedItems != null ? orderedItems.size() : 0) + " items.");
        return orderedItems;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
}
