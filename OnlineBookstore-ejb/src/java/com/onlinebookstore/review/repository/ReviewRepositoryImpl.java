package com.onlinebookstore.review.repository;

import com.onlinebookstore.review.entity.Reviews;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

/**
 * Implementation of IReviewRepository for Reviews data access operations.
 * 
 * @author ngnph
 */
@Stateless
public class ReviewRepositoryImpl implements IReviewRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public Reviews findById(Integer id) {
        if (id == null) {
            return null;
        }
        return entityManager.find(Reviews.class, id);
    }

    @Override
    public List<Reviews> findAll() {
        return entityManager.createQuery("SELECT r FROM Reviews r ORDER BY r.createdAt DESC", Reviews.class)
                .getResultList();
    }

    @Override
    public List<Reviews> findAllReviewByBookId(Integer bookId) {
        return entityManager.createQuery("SELECT r FROM Reviews r WHERE r.bookId.id = :bookId ORDER BY r.createdAt DESC", Reviews.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public List<Reviews> findReviewByUser(Integer userId) {
        return entityManager.createQuery("SELECT r FROM Reviews r WHERE r.userId.id = :userId ORDER BY r.createdAt DESC", Reviews.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Reviews> findReviewByUserIdAndBookId(Integer userId, Integer bookId) {
        return entityManager.createQuery("SELECT r FROM Reviews r WHERE r.userId.id = :userId AND r.bookId.id = :bookId ORDER BY r.createdAt DESC", Reviews.class)
                .setParameter("userId", userId)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public List<Reviews> findApprovedByBookId(Integer bookId) {
        return entityManager.createQuery("SELECT r FROM Reviews r WHERE r.bookId.id = :bookId AND r.isApproved = true ORDER BY r.createdAt DESC", Reviews.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public boolean existsByUserIdAndBookId(Integer userId, Integer bookId) {
        Long count = entityManager.createQuery("SELECT COUNT(r) FROM Reviews r WHERE r.userId.id = :userId AND r.bookId.id = :bookId", Long.class)
                .setParameter("userId", userId)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public Reviews save(Reviews review) {
        if (review.getCreatedAt() == null) {
            review.setCreatedAt(new Date());
        }
        if (review.getUpdatedAt() == null) {
            review.setUpdatedAt(new Date());
        }
        entityManager.persist(review);
        return review;
    }

    @Override
    public Reviews update(Reviews review) {
        review.setUpdatedAt(new Date());
        return entityManager.merge(review);
    }

    @Override
    public boolean deleteReview(Integer id) {
        Reviews review = findById(id);
        if (review != null) {
            entityManager.remove(review);
            return true;
        }
        return false;
    }

    @Override
    public int countReviewByBookId(Integer bookId) {
        Long count = entityManager.createQuery("SELECT COUNT(r) FROM Reviews r WHERE r.bookId.id = :bookId", Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public int countApprovedReviewByBookId(Integer bookId) {
        Long count = entityManager.createQuery("SELECT COUNT(r) FROM Reviews r WHERE r.bookId.id = :bookId AND r.isApproved = true", Long.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return count != null ? count.intValue() : 0;
    }

    @Override
    public Double getAverageRatingByBookId(Integer bookId) {
        Double avg = entityManager.createQuery("SELECT AVG(CAST(r.rating AS double)) FROM Reviews r WHERE r.bookId.id = :bookId AND r.isApproved = true", Double.class)
                .setParameter("bookId", bookId)
                .getSingleResult();
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }
}
