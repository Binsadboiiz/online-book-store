package com.onlinebookstore.review.dto;


/**
 * Response DTO representing review summary statistics for a book.
 * 
 * @author ngnph
 */
public class ReviewSummaryResponse {

    private Integer bookId;
    private Double averageRating;
    private Long totalReviews;

    public ReviewSummaryResponse() {
    }

    public ReviewSummaryResponse(Integer bookId, Double averageRating, Long totalReviews) {
        this.bookId = bookId;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}
