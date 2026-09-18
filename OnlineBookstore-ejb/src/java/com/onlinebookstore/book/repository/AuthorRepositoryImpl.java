package com.onlinebookstore.book.repository;

import com.onlinebookstore.book.entity.Authors;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class AuthorRepositoryImpl implements IAuthorRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public List<Authors> findAll() {
        return entityManager.createQuery("SELECT a FROM Authors a ORDER BY a.name ASC", Authors.class)
                .getResultList();
    }

    @Override
    public List<Authors> searchByName(String name) {
        return entityManager.createQuery("SELECT a FROM Authors a WHERE LOWER(a.name) LIKE LOWER(:name) ORDER BY a.name ASC", Authors.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Override
    public Authors findById(int id) {
        return entityManager.find(Authors.class, id);
    }

    @Override
    public Authors findByName(String name) {
        try {
            return entityManager.createQuery("SELECT a FROM Authors a WHERE LOWER(a.name) = LOWER(:name)", Authors.class)
                    .setParameter("name", name.trim())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Authors save(Authors author) {
        entityManager.persist(author);
        return author;
    }

    @Override
    public Authors update(Authors author) {
        return entityManager.merge(author);
    }

    @Override
    public void delete(int id) {
        Authors author = findById(id);
        if (author != null) {
            entityManager.remove(author);
        }
    }

    @Override
    public long countBooksByAuthorId(int authorId) {
        Long count = entityManager.createQuery("SELECT COUNT(b) FROM Books b WHERE b.authorId.id = :authorId", Long.class)
                .setParameter("authorId", authorId)
                .getSingleResult();
        return count != null ? count : 0;
    }
}
