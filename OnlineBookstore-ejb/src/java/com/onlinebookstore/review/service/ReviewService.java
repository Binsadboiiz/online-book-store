package com.onlinebookstore.review.service;

import com.onlinebookstore.book.entity.Books;
import com.onlinebookstore.book.repository.IBookRepository;
import com.onlinebookstore.common.dto.ApiResponse;
import com.onlinebookstore.common.exception.BadRequestException;
import com.onlinebookstore.common.exception.ResourceNotFoundException;
import com.onlinebookstore.order.repository.IOrderRepository;
import com.onlinebookstore.review.dto.CreateReviewRequest;
import com.onlinebookstore.review.dto.ReviewResponse;
import com.onlinebookstore.review.dto.ReviewSummaryResponse;
import com.onlinebookstore.review.entity.Reviews;
import com.onlinebookstore.review.repository.IReviewRepository;
import com.onlinebookstore.user.entity.Users;
import com.onlinebookstore.user.repository.IUserRepository;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ReviewService {

    @Inject
    private IReviewRepository reviewRepository;

    @Inject
    private IBookRepository bookRepository;

    @Inject
    private IUserRepository userRepository;

    @Inject
    private IOrderRepository orderRepository;

    public boolean canUserReview(Integer userId, Integer bookId) {
        if (userId == null || bookId == null) {
            return false;
        }
        return orderRepository.hasUserPurchasedBook(userId, bookId);
    }

    public ApiResponse<List<ReviewResponse>> getApprovedReviews(Integer bookId) {
        if (bookId == null) {
            throw new BadRequestException("Book ID is required");
        }
        List<Reviews> reviews = reviewRepository.findApprovedByBookId(bookId);
        List<ReviewResponse> responses = reviews.stream()
                .map(ReviewResponse::fromEntity)
                .collect(Collectors.toList());
        return ApiResponse.success("Reviews retrieved successfully", responses);
    }

    public ApiResponse<ReviewSummaryResponse> getReviewSummary(Integer bookId) {
        if (bookId == null) {
            throw new BadRequestException("Book ID is required");
        }
        Double avgRating = reviewRepository.getAverageRatingByBookId(bookId);
        int totalCount = reviewRepository.countApprovedReviewByBookId(bookId);

        ReviewSummaryResponse summary = new ReviewSummaryResponse(
                bookId,
                avgRating != null ? Math.round(avgRating * 10.0) / 10.0 : 0.0,
                (long) totalCount
        );
        return ApiResponse.success("Review summary retrieved", summary);
    }

    public ApiResponse<ReviewResponse> createReview(Integer userId, CreateReviewRequest request) {
        if (request.getBookId() == null) {
            throw new BadRequestException("Book ID is required");
        }
        if (request.getRating() == null || request.getRating() < 1 || request.getRating() > 5) {
            throw new BadRequestException("Rating must be between 1 and 5 stars");
        }

        if (!orderRepository.hasUserPurchasedBook(userId, request.getBookId())) {
            throw new BadRequestException("Chỉ người dùng đã mua sản phẩm này mới được viết đánh giá.");
        }

        Books book = bookRepository.findById(request.getBookId());
        if (book == null) {
            throw new ResourceNotFoundException("Book not found");
        }

        Users user = userRepository.findById(userId);
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Reviews review = new Reviews();
        review.setBookId(book);
        review.setUserId(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment() != null ? request.getComment().trim() : "");
        review.setIsApproved(true);

        Date now = new Date();
        review.setCreatedAt(now);
        review.setUpdatedAt(now);

        Reviews saved = reviewRepository.save(review);
        return ApiResponse.success("Review submitted successfully", ReviewResponse.fromEntity(saved));
    }
}
