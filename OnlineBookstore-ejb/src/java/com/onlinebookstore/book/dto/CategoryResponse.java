package com.onlinebookstore.book.dto;

import com.onlinebookstore.book.entity.Categories;
import java.io.Serializable;
import java.util.Date;

public class CategoryResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private String description;
    private boolean active;
    private long bookCount;
    private Date createdAt;

    public CategoryResponse() {
    }

    public CategoryResponse(Integer id, String name, String description, boolean active, long bookCount, Date createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.bookCount = bookCount;
        this.createdAt = createdAt;
    }

    public static CategoryResponse fromEntity(Categories category) {
        if (category == null) return null;
        long count = category.getBooksCollection() != null ? category.getBooksCollection().size() : 0;
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIsActive(),
                count,
                category.getCreatedAt()
        );
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getBookCount() {
        return bookCount;
    }

    public void setBookCount(long bookCount) {
        this.bookCount = bookCount;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
