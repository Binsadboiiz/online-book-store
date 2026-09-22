package com.onlinebookstore.book.service;

import com.onlinebookstore.book.dto.CategoryRequest;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.entity.Categories;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ConflictException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class CategoryService {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    public ApiResponse<List<CategoryResponse>> getCategories(String search) {
        List<Categories> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = entityManager.createQuery(
                    "SELECT c FROM Categories c WHERE LOWER(c.name) LIKE LOWER(:q) ORDER BY c.id DESC", Categories.class)
                    .setParameter("q", "%" + search.trim() + "%")
                    .getResultList();
        } else {
            categories = entityManager.createQuery(
                    "SELECT c FROM Categories c ORDER BY c.id DESC", Categories.class)
                    .getResultList();
        }

        List<CategoryResponse> responses = categories.stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());

        return ApiResponse.success("Categories retrieved successfully", responses);
    }

    public ApiResponse<CategoryResponse> getCategoryById(Integer id) {
        Categories category = entityManager.find(Categories.class, id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }
        return ApiResponse.success("Category retrieved successfully", toCategoryResponse(category));
    }

    public ApiResponse<CategoryResponse> createCategory(CategoryRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }

        List<Categories> existing = entityManager.createQuery(
                "SELECT c FROM Categories c WHERE LOWER(c.name) = LOWER(:name)", Categories.class)
                .setParameter("name", request.getName().trim())
                .getResultList();
        if (!existing.isEmpty()) {
            throw new ConflictException("Category with name '" + request.getName().trim() + "' already exists");
        }

        Categories category = new Categories();
        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        category.setIsActive(request.getActive() != null ? request.getActive() : true);

        Date now = new Date();
        category.setCreatedAt(now);
        category.setUpdatedAt(now);

        entityManager.persist(category);

        return ApiResponse.success("Category created successfully", toCategoryResponse(category));
    }

    public ApiResponse<CategoryResponse> updateCategory(Integer id, CategoryRequest request) {
        Categories category = entityManager.find(Categories.class, id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new BadRequestException("Category name cannot be empty");
        }

        List<Categories> existing = entityManager.createQuery(
                "SELECT c FROM Categories c WHERE LOWER(c.name) = LOWER(:name) AND c.id != :id", Categories.class)
                .setParameter("name", request.getName().trim())
                .setParameter("id", id)
                .getResultList();
        if (!existing.isEmpty()) {
            throw new ConflictException("Another category with name '" + request.getName().trim() + "' already exists");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        if (request.getActive() != null) {
            category.setIsActive(request.getActive());
        }
        category.setUpdatedAt(new Date());

        Categories updated = entityManager.merge(category);

        return ApiResponse.success("Category updated successfully", toCategoryResponse(updated));
    }

    public ApiResponse<CategoryResponse> toggleCategoryStatus(Integer id) {
        Categories category = entityManager.find(Categories.class, id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }

        category.setIsActive(!category.getIsActive());
        category.setUpdatedAt(new Date());

        Categories updated = entityManager.merge(category);
        return ApiResponse.success("Category status updated successfully", toCategoryResponse(updated));
    }

    public ApiResponse<String> deleteCategory(Integer id) {
        Categories category = entityManager.find(Categories.class, id);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found with ID: " + id);
        }

        Long count = entityManager.createQuery(
                "SELECT COUNT(b) FROM Books b WHERE b.categoryId.id = :catId", Long.class)
                .setParameter("catId", id)
                .getSingleResult();

        if (count != null && count > 0) {
            throw new ConflictException("Cannot delete category ID " + id + " because it is assigned to " + count + " book(s).");
        }

        entityManager.remove(category);
        return ApiResponse.success("Category deleted successfully", "Category ID " + id + " has been deleted.");
    }

    private CategoryResponse toCategoryResponse(Categories category) {
        Long count = entityManager.createQuery(
                "SELECT COUNT(b) FROM Books b WHERE b.categoryId.id = :catId", Long.class)
                .setParameter("catId", category.getId())
                .getSingleResult();
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIsActive(),
                count != null ? count : 0,
                category.getCreatedAt()
        );
    }
}
