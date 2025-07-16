/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author michael
 */
@Named("productBean")

@ViewScoped
public class ProductBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<InternetProducts> products;

    @Inject
    private ShoppingCartBean shoppingCartBean;

    @PostConstruct
    public void init() {
        products = new ArrayList<>();
        loadInternetProducts();
            System.out.println("Products in ProductBean: " + products.size());
    }

    private void loadInternetProducts() {
        try (Connection conn = mysqlConnector.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT ProductID, Name, Description, Price, Stock, image_path FROM Products ORDER BY Name ASC")) {

            while (rs.next()) {
                InternetProducts pkg = new InternetProducts();
                pkg.setProductID(rs.getInt("ProductID"));
                pkg.setName(rs.getString("Name"));
                pkg.setDescription(rs.getString("Description"));
                pkg.setPrice(rs.getDouble("Price"));
                pkg.setStock(rs.getInt("Stock"));
                 pkg.setSelectedQuantity(1); 
                pkg.setImagePath(rs.getString("image_path"));

                products.add(pkg);
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
         System.out.println("Loaded Products: " + products.size());
    }

    public void addToCart(InternetProducts product) {
    System.out.println("DEBUG: Entering addToCart method.");

 
    if (shoppingCartBean == null) {
        System.err.println("FATAL ERROR: shoppingCartBean is NULL! CDI injection failed. Cannot add to cart.");
        
        if (FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "System Error", "Shopping cart service not available. Please contact support."));
        }
        return; 
    }
    System.out.println("DEBUG: shoppingCartBean is successfully injected.");

   
    if (product == null) {
        System.err.println("ERROR: 'product' object passed to addToCart is NULL. Cannot process.");
        if (FacesContext.getCurrentInstance() != null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Product Error", "Selected product data is missing."));
        }
        return; 
    }
    System.out.println("DEBUG: Product received: ID=" + product.getProductID() + ", Name=" + product.getName() + ", Quantity=" + product.getSelectedQuantity() + ", Stock=" + product.getStock());

   
    int selectedQuantity = product.getSelectedQuantity();
    double stock = product.getStock(); 

    if (selectedQuantity <= 0) {
        System.out.println("DEBUG: Quantity is invalid (<= 0).");
        addMessage(FacesMessage.SEVERITY_WARN, "Invalid Quantity", "Quantity must be at least 1.");
    } else if (selectedQuantity > stock) {
        System.out.println("DEBUG: Quantity exceeds stock.");
        addMessage(FacesMessage.SEVERITY_WARN, "Out of Stock", "Not enough " + product.getName() + " in stock for the selected quantity (" + selectedQuantity + " requested, " + (int)stock + " available).");
    } else {

        System.out.println("DEBUG: Quantity is valid (" + selectedQuantity + "). Attempting to add " + product.getName() + " to cart.");
        try {
            shoppingCartBean.addItem(product, "Hardware", selectedQuantity);
            System.out.println("DEBUG: Successfully called shoppingCartBean.addItem().");
            addMessage(FacesMessage.SEVERITY_INFO, "Added to Cart", product.getName() + " x" + selectedQuantity + " added to cart.");
        } catch (Exception e) {
           
            System.err.println("ERROR: Exception caught during shoppingCartBean.addItem(): " + e.getMessage());
            e.printStackTrace(System.err); 
            addMessage(FacesMessage.SEVERITY_ERROR, "Cart Update Failed", "An error occurred while adding to cart: " + e.getMessage());
        }
    }
    System.out.println("DEBUG: Exiting addToCart method.");
}

    public List<InternetProducts> getProducts() {
        return products;
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
    }

    public static class InternetProducts implements Serializable {

        private static final long serialVersionUID = 1L;

        private int productID;
        private String name;
        private String description;
        private double price;
        private int stock;
        private String imagePath;
        private int selectedQuantity = 1;

        public int getProductID() {
            return productID;
        }

        public void setProductID(int productID) {
            this.productID = productID;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getStock() {
            return stock;
        }

        public void setStock(int stock) {
            this.stock = stock;
        }

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public int getSelectedQuantity() {
            return selectedQuantity;
        }

        public void setSelectedQuantity(int selectedQuantity) {
            this.selectedQuantity = selectedQuantity;
        }
    }
}
