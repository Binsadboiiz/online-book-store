package com.onlinebookstore.book.bean;

import com.onlinebookstore.book.dto.AuthorResponse;
import com.onlinebookstore.book.dto.BookRequest;
import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.dto.CategoryResponse;
import com.onlinebookstore.book.service.AuthorService;
import com.onlinebookstore.book.service.BookService;
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
import java.util.stream.Collectors;

@Named("adminBookBean")
@ViewScoped
public class AdminBookBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<BookResponse> books = new ArrayList<>();
    private List<CategoryResponse> categories = new ArrayList<>();
    private List<AuthorResponse> authors = new ArrayList<>();

    private String searchQuery;
    private Integer selectedCategoryId;
    private String selectedStockStatus;

    private BookRequest bookRequest = new BookRequest();
    private Integer selectedBookId;
    private String authorMode = "EXISTING";

    @Inject
    private BookService bookService;

    @Inject
    private AuthorService authorService;

    @PostConstruct
    public void init() {
        loadCategories();
        loadAuthors();
        loadBooks();
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

    public void loadAuthors() {
        try {
            ApiResponse<List<AuthorResponse>> res = authorService.getAuthors(null);
            if (res != null && res.isSuccess() && res.getData() != null) {
                authors = res.getData();
            }
        } catch (Exception e) {
            authors = new ArrayList<>();
        }
    }

    public void loadBooks() {
        try {
            ApiResponse<List<BookResponse>> res = bookService.getBooks(searchQuery, selectedCategoryId, null, null);
            if (res != null && res.isSuccess() && res.getData() != null) {
                List<BookResponse> list = res.getData();
                if ("INSTOCK".equalsIgnoreCase(selectedStockStatus)) {
                    list = list.stream().filter(b -> b.getStockQuantity() != null && b.getStockQuantity() > 0).collect(Collectors.toList());
                } else if ("OUTOFSTOCK".equalsIgnoreCase(selectedStockStatus)) {
                    list = list.stream().filter(b -> b.getStockQuantity() == null || b.getStockQuantity() <= 0).collect(Collectors.toList());
                }
                books = list;
            } else {
                books = new ArrayList<>();
            }
        } catch (Exception e) {
            books = new ArrayList<>();
        }
    }

    public void filterBooks() {
        loadBooks();
    }

    public void prepareAddBook() {
        selectedBookId = null;
        bookRequest = new BookRequest();
        bookRequest.setStockQuantity(10);
        bookRequest.setPublishedYear(2024);
        bookRequest.setLanguage("Tiếng Việt");
        authorMode = "EXISTING";
    }

    public void prepareEditBook(BookResponse b) {
        if (b == null) return;
        selectedBookId = b.getId();
        bookRequest = new BookRequest();
        bookRequest.setTitle(b.getTitle());
        bookRequest.setIsbn(b.getIsbn());
        bookRequest.setPrice(b.getPrice());
        bookRequest.setDiscountPrice(b.getDiscountPrice());
        bookRequest.setStockQuantity(b.getStockQuantity());
        bookRequest.setDescription(b.getDescription());
        bookRequest.setCoverImage(b.getCoverImage());
        bookRequest.setPublishedYear(b.getPublishedYear());
        bookRequest.setPages(b.getPages());
        bookRequest.setLanguage(b.getLanguage());
        bookRequest.setCategoryId(b.getCategoryId());
        bookRequest.setAuthorId(b.getAuthorId());
        authorMode = "EXISTING";
    }

    public void saveBook() {
        try {
            if ("NEW".equalsIgnoreCase(authorMode)) {
                bookRequest.setAuthorId(null);
            } else {
                bookRequest.setNewAuthorName(null);
                bookRequest.setNewAuthorBio(null);
            }

            ApiResponse<BookResponse> res;
            if (selectedBookId == null) {
                res = bookService.createBook(bookRequest);
            } else {
                res = bookService.updateBook(selectedBookId, bookRequest);
            }

            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Book saved successfully!", null));
                loadBooks();
                loadAuthors();
                prepareAddBook();
            } else {
                String msg = res != null ? res.getMessage() : "Failed to save book.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    public void deleteBook(Integer id) {
        if (id == null) return;
        try {
            ApiResponse<String> res = bookService.deleteBook(id);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Book deleted successfully.", null));
                loadBooks();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, res != null ? res.getMessage() : "Failed to delete book.", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
    }

    // Getters and Setters
    public List<BookResponse> getBooks() {
        return books;
    }

    public List<CategoryResponse> getCategories() {
        return categories;
    }

    public List<AuthorResponse> getAuthors() {
        return authors;
    }

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

    public String getSelectedStockStatus() {
        return selectedStockStatus;
    }

    public void setSelectedStockStatus(String selectedStockStatus) {
        this.selectedStockStatus = selectedStockStatus;
    }

    public BookRequest getBookRequest() {
        return bookRequest;
    }

    public void setBookRequest(BookRequest bookRequest) {
        this.bookRequest = bookRequest;
    }

    public Integer getSelectedBookId() {
        return selectedBookId;
    }

    public void setSelectedBookId(Integer selectedBookId) {
        this.selectedBookId = selectedBookId;
    }

    public String getAuthorMode() {
        return authorMode;
    }

    public void setAuthorMode(String authorMode) {
        this.authorMode = authorMode;
    }
}
