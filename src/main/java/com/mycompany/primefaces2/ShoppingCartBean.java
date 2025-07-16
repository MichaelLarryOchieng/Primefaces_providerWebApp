/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import com.mycompany.primefaces2.PackageBean.InternetPackage;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.annotation.PostConstruct;

@Named("shoppingCartBean")
@SessionScoped
public class ShoppingCartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CartService cartService;

    @Inject
    private LoginBean loginBean;

    private Cart currentDbCart;
    private List<ShoppingCart> cartItemsForDisplay;
    private int totalItemsInCart;
    private double cartTotalPrice;

    Integer getLoggedInUserId() {
        System.out.println("DEBUG: loginBean = " + loginBean);

        if (loginBean == null) {
            System.out.println("DEBUG: loginBean is NULL - injection failed");
            return null;
        }

        System.out.println("DEBUG: loginBean.getCurrentUser() = " + loginBean.getCurrentUser());

        if (loginBean.getCurrentUser() == null) {
            System.out.println("DEBUG: getCurrentUser() is NULL - user not logged in");
            return null;
        }

        Long userIdLong = loginBean.getCurrentUser().getId();
        System.out.println("DEBUG: userId = " + userIdLong);

        if (userIdLong != null) {
            return userIdLong.intValue();
        }

        System.out.println("DEBUG: userId is NULL");
        return null;
    }

    @PostConstruct
    public void init() {
        Integer userId = getLoggedInUserId();
        if (userId == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Required", "Please log in to manage your cart."));
            currentDbCart = null;
            cartItemsForDisplay = new ArrayList<>();
            totalItemsInCart = 0;
            cartTotalPrice = 0.0;
            return;
        }

        currentDbCart = cartService.getOrCreateActiveCartForUser(userId);
        if (currentDbCart == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Cart Error", "Could not initialize shopping cart for user " + userId + ". Check server logs."));
            cartItemsForDisplay = new ArrayList<>();
            totalItemsInCart = 0;
            cartTotalPrice = 0.0;
            return;
        }
        refreshCartDisplayData();
    }

    private void refreshCartDisplayData() {
        if (currentDbCart == null) {
            cartItemsForDisplay = new ArrayList<>();
            totalItemsInCart = 0;
            cartTotalPrice = 0.0;
            return;
        }

        if (currentDbCart.getCartId() <= 0) {
            cartItemsForDisplay = new ArrayList<>();
            totalItemsInCart = 0;
            cartTotalPrice = 0.0;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cart Initialization Error", "Cart ID is invalid. Please log in or try again."));
            return;
        }

        List<CartItem> dbCartItems = cartService.getCartItems(currentDbCart.getCartId());
        cartItemsForDisplay = new ArrayList<>();
        totalItemsInCart = 0;
        cartTotalPrice = 0.0;
        for (CartItem dbItem : dbCartItems) {
            String name = cartService.getProductName(dbItem.getProductType(), dbItem.getProductRefId());
            String imagePath = cartService.getProductImagePath(dbItem.getProductType(), dbItem.getProductRefId());
            ShoppingCart displayItem = new ShoppingCart(dbItem, name, imagePath);
            cartItemsForDisplay.add(displayItem);
            totalItemsInCart += displayItem.getQuantity();
            cartTotalPrice += displayItem.getTotalItemPrice();
        }
    }

    public void addItem(ProductBean.InternetProducts product, String type, int quantity) {
        if (currentDbCart == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cart Error", "Shopping cart is not initialized. Please log in."));
            return;
        }
        if (quantity <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Quantity", "Quantity must be at least 1."));
            return;
        }
        if ("HARDWARE".equals(type)) {
            int currentStock = cartService.getProductStock(type, product.getProductID());
            Optional<ShoppingCart> existingDisplayItem = cartItemsForDisplay.stream().filter(item -> item.getId() == product.getProductID() && item.getType().equals(type)).findFirst();
            int currentCartQuantity = existingDisplayItem.map(ShoppingCart::getQuantity).orElse(0);
            int requestedTotalQuantity = currentCartQuantity + quantity;
            if (requestedTotalQuantity > currentStock) {
                if (currentStock == 0) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Out of Stock", product.getName() + " is currently out of stock."));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock Limit", "Only " + currentStock + " of " + product.getName() + " available."));
                }
                return;
            }
        }
        cartService.addOrUpdateCartItem(currentDbCart.getCartId(), type, product.getProductID(), quantity, product.getPrice());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added to Cart", product.getName() + " (" + type + ") added."));
        refreshCartDisplayData();
    }

    public void addItem(InternetPackage pkg, int quantity) {
        if (currentDbCart == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Cart Error", "Shopping cart is not initialized. Please log in."));
            return;
        }
        String type = "PACKAGE";
        if (quantity <= 0) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid Quantity", "Quantity must be at least 1."));
            return;
        }
        cartService.addOrUpdateCartItem(currentDbCart.getCartId(), type, pkg.getPackageID(), quantity, pkg.getPrice());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Added to Cart", pkg.getName() + " (" + type + ") added."));
        refreshCartDisplayData();
    }

    public void removeItem(int productId, String type) {
        if (currentDbCart == null) {
            return;
        }
        cartService.removeCartItem(currentDbCart.getCartId(), type, productId);
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Item removed from cart."));
        refreshCartDisplayData();
    }

    public void updateItemQuantity(int cartItemId, String type, int productRefId, int newQuantity) {
        if (currentDbCart == null) {
            return;
        }

        if (newQuantity <= 0) {
            cartService.removeCartItem(currentDbCart.getCartId(), type, productRefId);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Item removed from cart (quantity 0)."));
        } else {
            if ("HARDWARE".equals(type)) {
                int currentStock = cartService.getProductStock(type, productRefId);
                if (newQuantity > currentStock) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Stock Limit", "Only " + currentStock + " of this item available. Quantity capped."));
                    newQuantity = currentStock;
                    if (newQuantity == 0) {
                        cartService.removeCartItem(currentDbCart.getCartId(), type, productRefId);
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Removed", "Item removed from cart (out of stock)."));
                        refreshCartDisplayData();
                        return;
                    }
                }
            }
            cartService.updateCartItemQuantity(cartItemId, newQuantity);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Updated", "Quantity updated."));
        }
        refreshCartDisplayData();
    }

    public void clearCart() {
        if (currentDbCart == null) {
            return;
        }
        cartService.clearCart(currentDbCart.getCartId());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Cart Cleared", "Your shopping cart has been cleared."));
        refreshCartDisplayData();
    }

    public int getTotalItemsInCart() {
        return totalItemsInCart;
    }

    public double getCartTotalPrice() {
        return cartTotalPrice;
    }

    public List<ShoppingCart> getCartItems() {
        return cartItemsForDisplay;
    }

    public ShoppingCart getCartItem(int productId, String type) {
        return cartItemsForDisplay.stream().filter(item -> item.getId() == productId && item.getType().equals(type)).findFirst().orElse(null);
    }

    public Cart getCurrentDbCart() {
        return currentDbCart;
    }
}
