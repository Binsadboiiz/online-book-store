/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.book.repository;

import com.onlinebookstore.book.entity.Books;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;

/**
 *
 * @author ngnph
 */
@Stateless
public class BookRepositoryImpl implements IBookRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public Books findById(Integer id) {
        return entityManager.find(Books.class, id);
    }

    @Override
    public Books findByIsbn(String isbn) {
        try {
            return entityManager.createQuery("SELECT b FROM Books b WHERE b.isbn = :isbn", Books.class)
                    .setParameter("isbn", isbn)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Books> findAll() {
        return entityManager.createQuery("SELECT b FROM Books b ORDER BY b.id DESC", Books.class)
                .getResultList();
    }

    @Override
    public List<Books> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }
        return entityManager.createQuery(
                "SELECT b FROM Books b WHERE LOWER(b.title) LIKE :keyword OR LOWER(b.description) LIKE :keyword OR LOWER(b.isbn) LIKE :keyword ORDER BY b.id DESC", 
                Books.class)
                .setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%")
                .getResultList();
    }

    @Override
    public Books save(Books book) {
        entityManager.persist(book);
        return book;
    }

    @Override
    public Books update(Books book) {
        return entityManager.merge(book);
    }

    @Override
    public boolean deleteById(Integer id) {
        Books book = findById(id);
        if (book != null) {
            entityManager.remove(book);
            return true;
        }
        return false;
    }

    @Override
    public List<Books> findByCategoryId(Integer categoryId) {
        return entityManager.createQuery("SELECT b FROM Books b WHERE b.categoryId.id = :categoryId ORDER BY b.id DESC", Books.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<Books> findByAuthorId(Integer authorId) {
        return entityManager.createQuery("SELECT b FROM Books b WHERE b.authorId.id = :authorId ORDER BY b.id DESC", Books.class)
                .setParameter("authorId", authorId)
                .getResultList();
    }

    @Override
    public List<Books> findByPublisherId(Integer publisherId) {
        return entityManager.createQuery("SELECT b FROM Books b WHERE b.publisherId.id = :publisherId ORDER BY b.id DESC", Books.class)
                .setParameter("publisherId", publisherId)
                .getResultList();
    }

    @Override
    public List<Books> findByActiveBooks() {
        return entityManager.createQuery("SELECT b FROM Books b WHERE b.isActive = true ORDER BY b.id DESC", Books.class)
                .getResultList();
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        Long count = entityManager.createQuery("SELECT COUNT(b) FROM Books b WHERE b.isbn = :isbn", Long.class)
                .setParameter("isbn", isbn)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public int deductStock(Integer bookId, int quantity) {
        return entityManager.createQuery(
                "UPDATE Books b SET b.stockQuantity = b.stockQuantity - :quantity, b.updatedAt = :now WHERE b.id = :bookId AND b.stockQuantity >= :quantity AND b.isActive = true")
                .setParameter("quantity", quantity)
                .setParameter("now", new java.util.Date())
                .setParameter("bookId", bookId)
                .executeUpdate();
    }

    @Override
    public int restock(Integer bookId, int quantity) {
        return entityManager.createQuery(
                "UPDATE Books b SET b.stockQuantity = b.stockQuantity + :quantity, b.updatedAt = :now WHERE b.id = :bookId")
                .setParameter("quantity", quantity)
                .setParameter("now", new java.util.Date())
                .setParameter("bookId", bookId)
                .executeUpdate();
    }
}
