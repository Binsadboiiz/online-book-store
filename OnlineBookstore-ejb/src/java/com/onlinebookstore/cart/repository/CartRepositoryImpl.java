/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.onlinebookstore.cart.repository;

import com.onlinebookstore.cart.entity.Cart;
import com.onlinebookstore.cart.entity.CartItems;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

/**
 * Implementation of ICartRepository interface using JPA EntityManager.
 * 
 * @author ngnph
 */
@Stateless
public class CartRepositoryImpl implements ICartRepository {

    @PersistenceContext(unitName = "OnlineBookstorePU")
    private EntityManager entityManager;

    @Override
    public Cart findByUserId(Integer userId) {
        try {
            return entityManager.createQuery("SELECT c FROM Cart c WHERE c.userId.id = :userId", Cart.class)
                    .setParameter("userId", userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public Cart findById(Integer id) {
        return entityManager.find(Cart.class, id);
    }

    @Override
    public Cart save(Cart cart) {
        entityManager.persist(cart);
        return cart;
    }

    @Override
    public Cart update(Cart cart) {
        return entityManager.merge(cart);
    }

    @Override
    public CartItems findCartItem(Integer cartId, Integer bookId) {
        try {
            return entityManager.createQuery(
                    "SELECT ci FROM CartItems ci WHERE ci.cartId.id = :cartId AND ci.bookId.id = :bookId", 
                    CartItems.class)
                    .setParameter("cartId", cartId)
                    .setParameter("bookId", bookId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public CartItems findCartItemById(Integer cartItemId) {
        return entityManager.find(CartItems.class, cartItemId);
    }

    @Override
    public CartItems saveItem(CartItems item) {
        entityManager.persist(item);
        return item;
    }

    @Override
    public CartItems updateItem(CartItems item) {
        return entityManager.merge(item);
    }

    @Override
    public boolean deleteItem(Integer cartItemId) {
        CartItems item = findCartItemById(cartItemId);
        if (item != null) {
            entityManager.remove(item);
            return true;
        }
        return false;
    }

    @Override
    public void clearCart(Integer cartId) {
        entityManager.createQuery("DELETE FROM CartItems ci WHERE ci.cartId.id = :cartId")
                .setParameter("cartId", cartId)
                .executeUpdate();
    }
}
