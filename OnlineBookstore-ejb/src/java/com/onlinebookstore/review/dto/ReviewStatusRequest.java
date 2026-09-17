package com.onlinebookstore.review.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating review approval status (Admin moderation).
 * 
 * @author ngnph
 */
public class ReviewStatusRequest {

    @NotNull(message = "Approval status is required")
    private Boolean isApproved;

    public ReviewStatusRequest() {
    }

    public ReviewStatusRequest(Boolean isApproved) {
        this.isApproved = isApproved;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}
