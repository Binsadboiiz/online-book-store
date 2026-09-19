package com.onlinebookstore.book.bean;

import com.onlinebookstore.auth.bean.AuthBean;
import com.onlinebookstore.book.dto.BookResponse;
import com.onlinebookstore.book.service.BookService;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.review.dto.ReviewResponse;
import com.onlinebookstore.review.dto.ReviewSummaryResponse;
import com.onlinebookstore.review.service.ReviewService;

import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("bookDetailBean")
@ViewScoped
public class BookDetailBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer bookId;
    private BookResponse book;
    private List<ReviewResponse> reviews = new ArrayList<>();
    private ReviewSummaryResponse summary;
    private boolean canUserReview;

    @Inject
    private BookService bookService;

    @Inject
    private ReviewService reviewService;

    @Inject
    private AuthBean authBean;

    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (bookId != null) {
                loadBookDetails();
            }
        }
    }

    public void loadBookDetails() {
        if (bookId == null) return;
        try {
            ApiResponse<BookResponse> res = bookService.getBookById(bookId);
            if (res != null && res.isSuccess()) {
                book = res.getData();
            }

            ApiResponse<List<ReviewResponse>> revRes = reviewService.getApprovedReviews(bookId);
            if (revRes != null && revRes.isSuccess() && revRes.getData() != null) {
                reviews = revRes.getData();
            }

            ApiResponse<ReviewSummaryResponse> sumRes = reviewService.getReviewSummary(bookId);
            if (sumRes != null && sumRes.isSuccess() && sumRes.getData() != null) {
                summary = sumRes.getData();
            }

            if (authBean.isLoggedIn()) {
                canUserReview = reviewService.canUserReview(authBean.getCurrentUserId(), bookId);
            } else {
                canUserReview = false;
            }
        } catch (Exception e) {
            book = null;
        }
    }

    // Getters and Setters
    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public BookResponse getBook() {
        return book;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public ReviewSummaryResponse getSummary() {
        return summary;
    }

    public boolean isHasUserPurchased() {
        if (authBean != null && authBean.isLoggedIn() && bookId != null) {
            try {
                return reviewService.hasUserPurchased(authBean.getCurrentUserId(), bookId);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean isHasUserReviewed() {
        if (authBean != null && authBean.isLoggedIn() && bookId != null) {
            try {
                return reviewService.hasUserReviewed(authBean.getCurrentUserId(), bookId);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    public boolean isCanUserReview() {
        if (authBean != null && authBean.isLoggedIn() && bookId != null) {
            try {
                return reviewService.canUserReview(authBean.getCurrentUserId(), bookId);
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }
}
