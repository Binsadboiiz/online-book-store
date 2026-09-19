package com.onlinebookstore.book.bean;

import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.service.BookService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.review.dto.ReviewSummaryResponse;
import com.onlinebookstore.review.service.ReviewService;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("catalogBean")
@ViewScoped
public class CatalogBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String searchQuery;
    private Integer selectedCategoryId;

    private List<BookResponse> topSellingBooks = new ArrayList<>();
    private List<BookResponse> catalogBooks = new ArrayList<>();
    private List<CategoryResponse> categories = new ArrayList<>();

    private BookResponse selectedPreviewBook;
    private ReviewSummaryResponse selectedPreviewSummary;

    @Inject
    private BookService bookService;

    @Inject
    private ReviewService reviewService;

    @PostConstruct
    public void init() {
        loadCategories();
        loadTopSelling();
        loadCatalog();
    }

    public void loadCategories() {
        try {
            ApiResponse<List<CategoryResponse>> res = bookService.getCategories();
            if (res != null && res.isSuccess() && res.getData() != null) {
                categories = res.getData();
            }
        } catch (Exception e) {
            categories = new ArrayList<>();
        }
    }

    public void loadTopSelling() {
        try {
            ApiResponse<List<BookResponse>> res = bookService.getTopSellingBooks(10);
            if (res != null && res.isSuccess() && res.getData() != null && !res.getData().isEmpty()) {
                topSellingBooks = res.getData();
            } else {
                ApiResponse<List<BookResponse>> allRes = bookService.getBooks(null, null, null, null);
                if (allRes != null && allRes.getData() != null) {
                    List<BookResponse> all = allRes.getData();
                    topSellingBooks = all.size() > 10 ? all.subList(0, 10) : all;
                }
            }
        } catch (Exception e) {
            topSellingBooks = new ArrayList<>();
        }
    }

    public void loadCatalog() {
        try {
            ApiResponse<List<BookResponse>> res = bookService.getBooks(searchQuery, selectedCategoryId, null, null);
            if (res != null && res.isSuccess() && res.getData() != null) {
                catalogBooks = res.getData();
            } else {
                catalogBooks = new ArrayList<>();
            }
        } catch (Exception e) {
            catalogBooks = new ArrayList<>();
        }
    }

    public void search() {
        loadCatalog();
    }

    public void filterByCategory() {
        loadCatalog();
    }

    public void resetFilters() {
        this.searchQuery = null;
        this.selectedCategoryId = null;
        loadCatalog();
    }

    public void selectBookForPreview(Integer bookId) {
        if (bookId == null) return;
        try {
            ApiResponse<BookResponse> res = bookService.getBookById(bookId);
            if (res != null && res.isSuccess()) {
                this.selectedPreviewBook = res.getData();
                ApiResponse<ReviewSummaryResponse> sumRes = reviewService.getReviewSummary(bookId);
                if (sumRes != null && sumRes.isSuccess()) {
                    this.selectedPreviewSummary = sumRes.getData();
                } else {
                    this.selectedPreviewSummary = null;
                }
            }
        } catch (Exception e) {
            this.selectedPreviewBook = null;
            this.selectedPreviewSummary = null;
        }
    }

    // Getters and Setters
    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Integer getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public void setSelectedCategoryId(Integer selectedCategoryId) {
        this.selectedCategoryId = selectedCategoryId;
    }

    public List<BookResponse> getTopSellingBooks() {
        return topSellingBooks;
    }

    public List<BookResponse> getCatalogBooks() {
        return catalogBooks;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public BookResponse getSelectedPreviewBook() {
        return selectedPreviewBook;
    }

    public ReviewSummaryResponse getSelectedPreviewSummary() {
        return selectedPreviewSummary;
    }
}
