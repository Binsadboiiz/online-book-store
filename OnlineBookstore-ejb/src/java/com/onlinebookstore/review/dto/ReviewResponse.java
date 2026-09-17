package com.onlinebookstore.review.dto;

import com.onlinebookstore.review.entity.Reviews;
import java.util.Date;

/**
 * Response DTO representing review details.
 * 
 * @author ngnph
 */
public class ReviewResponse {


    private Integer id;
    private int rating;
    private String comment;
    private boolean isApproved;
    private Date createdAt;
    private Date updatedAt;
    private Integer bookId;
    private String bookTitle;
    private Integer userId;
    private String username;
    private String userFullName;

    public ReviewResponse() {
    }

    public ReviewResponse(Integer id, int rating, String comment, boolean isApproved, 
                          Date createdAt, Date updatedAt, Integer bookId, String bookTitle, 
                          Integer userId, String username, String userFullName) {
        this.id = id;
        this.rating = rating;
        this.comment = comment;
        this.isApproved = isApproved;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.userId = userId;
        this.username = username;
        this.userFullName = userFullName;
    }

    public static ReviewResponse fromEntity(Reviews review) {
        if (review == null) {
            return null;
        }

        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setIsApproved(review.getIsApproved());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());

        if (review.getBookId() != null) {
            response.setBookId(review.getBookId().getId());
            response.setBookTitle(review.getBookId().getTitle());
        }

        if (review.getUserId() != null) {
            response.setUserId(review.getUserId().getId());
            response.setUsername(review.getUserId().getUsername());
            response.setUserFullName(review.getUserId().getFullName());
        }

        return response;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }
}
