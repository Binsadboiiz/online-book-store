package com.onlinebookstore.review.repository;

import com.onlinebookstore.review.entity.Reviews;
import java.util.List;

/**
 * Interface repository for managing Reviews entities.
 * 
 * @author ngnph
 */
public interface IReviewRepository {

    Reviews findById(Integer id);

    List<Reviews> findAll();

    List<Reviews> findAllReviewByBookId(Integer bookId);

    List<Reviews> findReviewByUser(Integer userId);

    List<Reviews> findReviewByUserIdAndBookId(Integer userId, Integer bookId);

    List<Reviews> findApprovedByBookId(Integer bookId);

    boolean existsByUserIdAndBookId(Integer userId, Integer bookId);

    Reviews save(Reviews review);

    Reviews update(Reviews review);

    boolean deleteReview(Integer id);

    int countReviewByBookId(Integer bookId);

    int countApprovedReviewByBookId(Integer bookId);

    Double getAverageRatingByBookId(Integer bookId);
}
