/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.book.dto;

import com.onlinebookstore.book.entity.Books;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ngnph
 */
public class BookResponse {
    private Integer id;
    private String title;
    private String isbn;
    private Integer authorId;
    private String authorName;
    private Integer categoryId;
    private String categoryName;
    private Integer publisherId;
    private String publisherName;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private BigDecimal finalPrice;
    private Integer stockQuantity;
    private String description;
    private String coverImage;
    private Integer publishedYear;
    private Integer pages;
    private String language;
    private Boolean active;
    private Date createdAt;
    private Date updatedAt;

    public BookResponse() {
    }

    public static BookResponse fromEntity(Books book) {
        if (book == null) return null;
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setIsbn(book.getIsbn());
        
        if (book.getAuthorId() != null) {
            response.setAuthorId(book.getAuthorId().getId());
            response.setAuthorName(book.getAuthorId().getName());
        }
        if (book.getCategoryId() != null) {
            response.setCategoryId(book.getCategoryId().getId());
            response.setCategoryName(book.getCategoryId().getName());
        }
        if (book.getPublisherId() != null) {
            response.setPublisherId(book.getPublisherId().getId());
            response.setPublisherName(book.getPublisherId().getName());
        }
        
        response.setPrice(book.getPrice());
        response.setDiscountPrice(book.getDiscountPrice());
        response.setFinalPrice(book.getDiscountPrice() != null && book.getDiscountPrice().compareTo(BigDecimal.ZERO) > 0 
                ? book.getDiscountPrice() : book.getPrice());
        
        response.setStockQuantity(book.getStockQuantity());
        response.setDescription(book.getDescription());
        response.setCoverImage(book.getCoverImage());
        response.setPublishedYear(book.getPublishedYear());
        response.setPages(book.getPages());
        response.setLanguage(book.getLanguage());
        response.setActive(book.getIsActive());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
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

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }

    public void setFinalPrice(BigDecimal finalPrice) {
        this.finalPrice = finalPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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
