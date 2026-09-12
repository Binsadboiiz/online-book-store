/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.dto;

import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Response DTO representing the user's shopping cart.
 * 
 * @author ngnph
 */
public class CartResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private List<CartItemResponse> items;
    private Integer totalQuantity;
    private BigDecimal totalAmount;
    private Date createdAt;
    private Date updatedAt;

    public CartResponse() {
        this.items = new ArrayList<>();
        this.totalQuantity = 0;
        this.totalAmount = BigDecimal.ZERO;
    }

    public static CartResponse fromEntity(Cart cart) {
        if (cart == null) {
            return null;
        }

        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        if (cart.getUserId() != null) {
            response.setUserId(cart.getUserId().getId());
        }
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());

        List<CartItemResponse> itemResponses = new ArrayList<>();
        int totalQty = 0;
        BigDecimal grandTotal = BigDecimal.ZERO;

        if (cart.getCartItemsCollection() != null) {
            for (CartItems item : cart.getCartItemsCollection()) {
                CartItemResponse itemDto = CartItemResponse.fromEntity(item);
                if (itemDto != null) {
                    itemResponses.add(itemDto);
                    if (itemDto.getQuantity() != null) {
                        totalQty += itemDto.getQuantity();
                    }
                    if (itemDto.getSubtotal() != null) {
                        grandTotal = grandTotal.add(itemDto.getSubtotal());
                    }
                }
            }
        }

        response.setItems(itemResponses);
        response.setTotalQuantity(totalQty);
        response.setTotalAmount(grandTotal);

        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public Integer getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Integer totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
