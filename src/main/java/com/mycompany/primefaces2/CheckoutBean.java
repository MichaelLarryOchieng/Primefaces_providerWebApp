/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.primefaces2;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
// import com.mycompany.primefaces2.ReceiptBean; // This import is redundant if in same package, but harmless

@Named("checkoutBean")
@ViewScoped
public class CheckoutBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ShoppingCartBean shoppingCartBean;

    @Inject
    private OrderService orderService;

    @Inject 
    private ReceiptBean receiptBean; 



    private String contactEmail;
    private String countyRegion;
    private String firstName;
    private String lastName;
    private String address;
    private String postalCode;
    private String city;
    private String customerContact;

    public String placeOrder() {
        FacesContext context = FacesContext.getCurrentInstance();

        if (shoppingCartBean.getCartItems().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Order Error", "Your cart is empty. Please add items before placing an order."));
            return null;
        }

        if (contactEmail == null || contactEmail.trim().isEmpty() ||
            countyRegion == null || countyRegion.trim().isEmpty() ||
            firstName == null || firstName.trim().isEmpty() ||
            lastName == null || lastName.trim().isEmpty() ||
            address == null || address.trim().isEmpty() ||
            postalCode == null || postalCode.trim().isEmpty() ||
            city == null || city.trim().isEmpty()) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", "All contact and shipping fields are required."));
            return null;
        }

        try {
            Order newOrder = new Order();
            newOrder.setUserId(shoppingCartBean.getLoggedInUserId());
            newOrder.setCartId(shoppingCartBean.getCurrentDbCart().getCartId());
            newOrder.setOrderDate(LocalDateTime.now());
            newOrder.setStatus("PENDING");
            newOrder.setTotalAmount(BigDecimal.valueOf(shoppingCartBean.getCartTotalPrice()));
            newOrder.setCustomerEmail(contactEmail);
            newOrder.setCustomerContact(customerContact);
            newOrder.setFirstName(firstName);
            newOrder.setLastName(lastName);
            newOrder.setShippingAddress(address);
            newOrder.setShippingCity(city);
            newOrder.setShippingPostalCode(postalCode);
            newOrder.setShippingCountyRegion(countyRegion);

            List<OrderedItem> orderedItems = shoppingCartBean.getCartItems().stream()
                .map(displayItem -> {
                    OrderedItem item = new OrderedItem();
                    item.setProductType(displayItem.getType());
                    item.setProductRefId(displayItem.getId());
                    item.setProductName(displayItem.getName());
                    item.setProductImagePath(displayItem.getImagePath());
                    item.setQuantity(displayItem.getQuantity());
                    item.setPriceAtTimeOfAddition(BigDecimal.valueOf(displayItem.getPrice()));
                    return item;
                })
                .collect(Collectors.toList());

            Integer newOrderId = orderService.createOrder(newOrder, orderedItems);

            if (newOrderId != null) {
                
                receiptBean.setConfirmationDetails(
                    newOrderId,
                    contactEmail,
                    firstName, lastName, address, city, postalCode, countyRegion,
                    BigDecimal.valueOf(shoppingCartBean.getCartTotalPrice()),
                    orderedItems
                );

                shoppingCartBean.clearCart();

                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order Placed! Thank You for shopping with us!", "Your order #" + newOrderId + " has been successfully placed."));
                return "receipt.xhtml?faces-redirect=true";
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Order Failed", "Failed to save your order to the database. Please try again."));
                return null;
            }

        } catch (Exception e) {
            System.err.println("Error while placing order: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Order Failed", "An unexpected error occurred: " + e.getMessage()));
            return null;
        }
    }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getCountyRegion() { return countyRegion; }
    public void setCountyRegion(String countyRegion) { this.countyRegion = countyRegion; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCustomerContact() { return customerContact; }
    public void setCustomerContact(String customerContact) { this.customerContact = customerContact; }
}



