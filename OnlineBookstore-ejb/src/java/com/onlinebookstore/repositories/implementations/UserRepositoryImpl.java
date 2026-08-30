/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.repositories.implementations;

import com.onlinebookstore.entities.Users;
import com.onlinebookstore.repositories.interfaces.IUserRepository;

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
public class UserRepositoryImpl implements IUserRepository {
    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;
    
    @Override
    public Users findById(int id) {
        return entityManager.find(Users.class, id);
    }

    @Override
    public Users findByUsername(String username) {
        try {
            return entityManager.createQuery(
            "SELECT u FROM Users u WHERE u.username= :username", Users.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Users findByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM Users u WHERE u.email = :email", Users.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

    @Override
    public Users findByUsernameOrEmail(String usernameOrEmail) {
        try {
            return entityManager.createQuery("SELECT u FROM Users u WHERE u.username = :value OR u.email = :value", Users.class)
                    .setParameter("value", usernameOrEmail)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<Users> findAll() {
        try {
            return entityManager.createQuery("SELECT u FROM Users u ORDER BY u.id DESC", Users.class)
                    .getResultList();
        } catch(NoResultException e) {
            return null;
        }
    }

    @Override
    public Users save(Users user) {
        entityManager.persist(user);
        return user;
    }

    @Override
    public Users update(Users user) {
        return entityManager.merge(user);
    }

    @Override
    public void delete(int id) {
        Users user = findById(id);
        if(user != null) entityManager.remove(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Users u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = entityManager.createQuery("SELECT COUNT(u) FROM Users u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }
}
