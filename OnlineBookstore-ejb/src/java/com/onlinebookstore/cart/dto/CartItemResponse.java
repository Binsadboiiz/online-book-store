/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.dto;

import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.cart.entity.CartItems;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Response DTO for an item within a shopping cart.
 * 
 * @author ngnph
 */
public class CartItemResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String bookCoverImage;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal subtotal;
    private Date createdAt;
    private Date updatedAt;

    public CartItemResponse() {
    }

    public static CartItemResponse fromEntity(CartItems item) {
        if (item == null) {
            return null;
        }

        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setQuantity(item.getQuantity());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());

        Books book = item.getBookId();
        if (book != null) {
            response.setBookId(book.getId());
            response.setBookTitle(book.getTitle());
            response.setBookCoverImage(book.getCoverImage());
            response.setPrice(book.getPrice());
            response.setDiscountPrice(book.getDiscountPrice());

            BigDecimal effectivePrice = (book.getDiscountPrice() != null && book.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0)
                    ? book.getDiscountPrice()
                    : book.getPrice();
            response.setUnitPrice(effectivePrice);

            if (effectivePrice != null) {
                response.setSubtotal(effectivePrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            } else {
                response.setSubtotal(BigDecimal.ZERO);
            }
        } else {
            response.setUnitPrice(BigDecimal.ZERO);
            response.setSubtotal(BigDecimal.ZERO);
        }

        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookCoverImage() {
        return bookCoverImage;
    }

    public void setBookCoverImage(String bookCoverImage) {
        this.bookCoverImage = bookCoverImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
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
