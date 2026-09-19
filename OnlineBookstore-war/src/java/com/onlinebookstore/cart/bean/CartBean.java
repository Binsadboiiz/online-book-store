package com.onlinebookstore.cart.bean;

import com.onlinebookstore.auth.bean.AuthBean;
import com.onlinebookstore.cart.dto.AddToCartRequest;
import com.onlinebookstore.cart.dto.CartResponse;
import com.onlinebookstore.cart.dto.UpdateCartItemRequest;
import com.onlinebookstore.cart.service.ICartService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.order.dto.CreateOrderRequest;
import com.onlinebookstore.order.dto.OrderResponse;
import com.onlinebookstore.order.service.IOrderService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("cartBean")
@ViewScoped
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private CartResponse cart;

    // Checkout form fields
    private String recipientName;
    private String recipientPhone;
    private String shippingAddress;
    private String paymentMethod = "COD";
    private String note;

    @Inject
    private ICartService cartService;

    @Inject
    private IOrderService orderService;

    @Inject
    private AuthBean authBean;

    @PostConstruct
    public void init() {
        loadCart();
        if (authBean.isLoggedIn() && authBean.getCurrentUser() != null) {
            recipientName = authBean.getCurrentUser().getFullName();
        }
    }

    public void loadCart() {
        if (!authBean.isLoggedIn()) {
            cart = null;
            return;
        }
        try {
            ApiResponse<CartResponse> res = cartService.getCartByUserId(authBean.getCurrentUserId());
            if (res != null && res.isSuccess()) {
                cart = res.getData();
            }
        } catch (Exception e) {
            cart = null;
        }
    }

    public void addToCart(Integer bookId, int quantity) {
        if (!authBean.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please sign in to add items to your shopping cart.", null));
            return;
        }

        if (authBean.isManager() || authBean.isAdmin()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Managers and Admins are restricted from shopping cart operations.", null));
            return;
        }

        try {
            AddToCartRequest req = new AddToCartRequest();
            req.setBookId(bookId);
            req.setQuantity(quantity);

            ApiResponse<CartResponse> res = cartService.addToCart(authBean.getCurrentUserId(), req);
            if (res != null && res.isSuccess()) {
                cart = res.getData();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item added to Shopping Cart!", null));
            } else {
                String msg = res != null ? res.getMessage() : "Failed to add item to cart.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void updateQuantity(Integer cartItemId, int quantity) {
        if (!authBean.isLoggedIn() || cartItemId == null) return;
        try {
            UpdateCartItemRequest req = new UpdateCartItemRequest();
            req.setQuantity(quantity);

            ApiResponse<CartResponse> res = cartService.updateCartItem(authBean.getCurrentUserId(), cartItemId, req);
            if (res != null && res.isSuccess()) {
                cart = res.getData();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Error updating cart", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void removeItem(Integer cartItemId) {
        if (!authBean.isLoggedIn() || cartItemId == null) return;
        try {
            ApiResponse<CartResponse> res = cartService.removeCartItem(authBean.getCurrentUserId(), cartItemId);
            if (res != null && res.isSuccess()) {
                cart = res.getData();
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Item removed from cart.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public String checkout() {
        if (!authBean.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please sign in to complete checkout.", null));
            return null;
        }

        if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Your shopping cart is empty.", null));
            return null;
        }

        try {
            CreateOrderRequest req = new CreateOrderRequest();
            req.setRecipientName(recipientName);
            req.setRecipientPhone(recipientPhone);
            req.setShippingAddress(shippingAddress);
            req.setPaymentMethod(paymentMethod);
            req.setNote(note);

            ApiResponse<OrderResponse> res = orderService.createOrder(authBean.getCurrentUserId(), req);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Order placed successfully!", null));
                return "/pages/customer/orders.xhtml?faces-redirect=true";
            } else {
                String msg = res != null ? res.getMessage() : "Failed to place order.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
                return null;
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
            return null;
        }
    }

    public int getItemCount() {
        if (cart == null || cart.getItems() == null) return 0;
        return cart.getItems().stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum();
    }

    // Getters and Setters
    public CartResponse getCart() {
        return cart;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
