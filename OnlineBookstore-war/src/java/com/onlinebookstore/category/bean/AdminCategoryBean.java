package com.onlinebookstore.category.bean;

import com.onlinebookstore.book.dto.CategoryRequest;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.service.CategoryService;
import com.onlinebookstore.common.dto.ApiResponse;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("adminCategoryBean")
@ViewScoped
public class AdminCategoryBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<CategoryResponse> categories = new ArrayList<>();
    private String searchQuery;

    private CategoryRequest categoryRequest = new CategoryRequest();
    private Integer selectedCategoryId;

    @Inject
    private CategoryService categoryService;

    @PostConstruct
    public void init() {
        loadCategories();
    }

    public void loadCategories() {
        try {
            ApiResponse<List<CategoryResponse>> res = categoryService.getCategories(searchQuery);
            if (res != null && res.isSuccess() && res.getData() != null) {
                categories = res.getData();
            } else {
                categories = new ArrayList<>();
            }
        } catch (Exception e) {
            categories = new ArrayList<>();
        }
    }

    public void filterCategories() {
        loadCategories();
    }

    public void prepareAddCategory() {
        selectedCategoryId = null;
        categoryRequest = new CategoryRequest();
        categoryRequest.setActive(true);
    }

    public void prepareEditCategory(CategoryResponse c) {
        if (c == null) return;
        selectedCategoryId = c.getId();
        categoryRequest = new CategoryRequest();
        categoryRequest.setName(c.getName());
        categoryRequest.setDescription(c.getDescription());
        categoryRequest.setActive(c.isActive());
    }

    public void saveCategory() {
        try {
            ApiResponse<CategoryResponse> res;
            if (selectedCategoryId == null) {
                res = categoryService.createCategory(categoryRequest);
            } else {
                res = categoryService.updateCategory(selectedCategoryId, categoryRequest);
            }

            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Category saved successfully!", null));
                loadCategories();
                prepareAddCategory();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to save category.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void toggleCategoryStatus(Integer id) {
        if (id == null) return;
        try {
            ApiResponse<CategoryResponse> res = categoryService.toggleCategoryStatus(id);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Category status updated.", null));
                loadCategories();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to update status.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void deleteCategory(Integer id) {
        if (id == null) return;
        try {
            ApiResponse<String> res = categoryService.deleteCategory(id);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Category deleted successfully!", null));
                loadCategories();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to delete category.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public CategoryRequest getCategoryRequest() {
        return categoryRequest;
    }

    public void setCategoryRequest(CategoryRequest categoryRequest) {
        this.categoryRequest = categoryRequest;
    }

    public Integer getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Integer selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }
}
