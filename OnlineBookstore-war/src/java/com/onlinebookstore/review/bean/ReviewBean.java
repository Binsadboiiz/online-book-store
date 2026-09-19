package com.onlinebookstore.review.bean;

import com.onlinebookstore.auth.bean.AuthBean;
import com.onlinebookstore.book.bean.BookDetailBean;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.review.dto.CreateReviewRequest;
import com.onlinebookstore.review.dto.ReviewResponse;
import com.onlinebookstore.review.service.ReviewService;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("reviewBean")
@RequestScoped
public class ReviewBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer bookId;
    private Integer rating = 5;
    private String comment;

    @Inject
    private ReviewService reviewService;

    @Inject
    private AuthBean authBean;

    @Inject
    private BookDetailBean bookDetailBean;

    public String submitReview() {
        if (!authBean.isLoggedIn()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please sign in to leave a review.", null));
            return null;
        }

        if (bookId == null && bookDetailBean != null) {
            bookId = bookDetailBean.getBookId();
        }

        if (bookId == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Book ID is missing.", null));
            return null;
        }

        if (comment == null || comment.trim().isEmpty()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Please enter your review comment.", null));
            return null;
        }

        try {
            CreateReviewRequest req = new CreateReviewRequest();
            req.setBookId(bookId);
            req.setRating(rating != null ? rating : 5);
            req.setComment(comment.trim());

            ApiResponse<ReviewResponse> res = reviewService.createReview(authBean.getCurrentUserId(), req);
            if (res != null && res.isSuccess()) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Thank you! Your review has been submitted.", null));
                comment = "";
                if (bookDetailBean != null) {
                    bookDetailBean.loadBookDetails();
                }
            } else {
                String msg = (res != null && res.getMessage() != null) ? res.getMessage() : "Failed to submit review.";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
            }
        } catch (Exception e) {
            String errorMsg = getRootErrorMessage(e);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMsg, null));
        }
        return null;
    }

    private String getRootErrorMessage(Throwable t) {
        if (t == null) return "Failed to submit review.";
        Throwable root = t;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        String msg = root.getMessage();
        if (msg == null || msg.trim().isEmpty()) {
            msg = t.getMessage();
        }
        return (msg != null && !msg.trim().isEmpty()) ? msg : "Failed to submit review.";
    }

    // Getters and Setters
    public Integer getBookId() {
        if (bookId == null && bookDetailBean != null) {
            return bookDetailBean.getBookId();
        }
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
