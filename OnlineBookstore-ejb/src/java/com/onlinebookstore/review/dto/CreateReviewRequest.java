package com.onlinebookstore.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


/**
 * Request DTO for creating a new review.
 * 
 * @author ngnph
 */
public class CreateReviewRequest{

    @NotNull(message = "Book ID is required")
    @Min(value = 1, message = "Book ID must be valid")
    private Integer bookId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must not exceed 5")
    private Integer rating;

    @Size(max = 2000, message = "Comment must not exceed 2000 characters")
    private String comment;

    public CreateReviewRequest() {
    }

    public CreateReviewRequest(Integer bookId, Integer rating, String comment) {
        this.bookId = bookId;
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getBookId() {
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
