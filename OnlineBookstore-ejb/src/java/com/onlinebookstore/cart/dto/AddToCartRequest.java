/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Request DTO for adding an item to the shopping cart.
 * 
 * @author ngnph
 */
public class AddToCartRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "Book ID is required")
    @Min(value = 1, message = "Book ID must be valid")
    private Integer bookId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public AddToCartRequest() {
    }

    public AddToCartRequest(Integer bookId, Integer quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
