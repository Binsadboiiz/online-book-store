package com.onlinebookstore.order.dto;

import com.onlinebookstore.order.entity.OrderItems;
import java.io.Serializable;
import java.math.BigDecimal;

public class OrderItemResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String coverImage;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;

    public OrderItemResponse() {
    }

    public static OrderItemResponse fromEntity(OrderItems item) {
        if (item == null) {
            return null;
        }

        OrderItemResponse dto = new OrderItemResponse();
        dto.setId(item.getId());
        if (item.getBookId() != null) {
            dto.setBookId(item.getBookId().getId());
            dto.setCoverImage(item.getBookId().getCoverImage());
        }
        dto.setBookTitle(item.getBookTitle());
        dto.setPrice(item.getPrice());
        dto.setQuantity(item.getQuantity());
        dto.setSubtotal(item.getSubtotal());
        return dto;
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

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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
}
