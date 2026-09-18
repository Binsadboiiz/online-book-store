package com.onlinebookstore.book.dto;

import com.onlinebookstore.book.entity.Categories;

public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;

    public CategoryResponse() {
    }

    public CategoryResponse(Integer id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static CategoryResponse fromEntity(Categories category) {
        if (category == null) return null;
        return new CategoryResponse(category.getId(), category.getName(), category.getDescription());
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
}
